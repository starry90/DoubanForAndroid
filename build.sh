#########################################################################
# File Name: build.sh
# 注意:jenkins上用的是dash不是bash 语法上有区别!!!!
#########################################################################
#! /bin/sh
set -u #使用未赋值的变量中断脚本执行
# 脚本有一处报处会导致jenkins构建失败
# -e意味着，即使只有一个命令失败，shell脚本也会退出，如果您对该命令执行错误检查，则会退出
set -e #任何语句的执行结果不是true则中断脚本执行

# 问题出现：
# Jenkins一直都构建成功，今天突然报错：Jenkins Build step 'Execute shell' marked build as failure
# 问题原因：
# By default Jenkins take /bin/sh -xe and this means -x will print each and every command.And the other option -e,
# which causes shell to stop running a script immediately when any command exits with non-zero (when any command fails) exit code.
# 解决办法：
# 通过问题原因基本确定是因为-e或者-x导致构建失败(我这边是因为-x导致磁盘空间不足)，可以选择修复脚错误、或设置set +e
# https://cloud.tencent.com/developer/ask/sof/96444

#项目根目录
project_path=`pwd`
#最终输出目录
out_path="$project_path/outputs/"

echo ">>> Shell build apk start"
rm -rf $out_path
mkdir $out_path

#增加执行权限
chmod +x gradlew
#clean
./gradlew clean
#打包所有渠道
./gradlew assemble
echo "copy *.apk, *.jar and mapping.txt to "$out_path
cp app/build/outputs/apk/*/*.apk $out_path
cp app/build/outputs/mapping/*/mapping.txt $out_path
# ./gradlew Http:makeJar

cd $out_path
du -b *.apk >> build-info.txt
echo "" >> build-info.txt
md5sum *.apk >> build-info.txt
git log -n 10 --stat >> git-log.txt

git_version=`git rev-list HEAD --count`
touch "git-${git_version}"

echo ">>> Shell build apk end"