#########################################################################
# File Name: build.sh
# 注意:jenkins上用的是dash不是bash 语法上有区别!!!!
#########################################################################
#! /bin/sh
set -u #使用未赋值的变量中断脚本执行
set -e #任何语句的执行结果不是true则中断脚本执行

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
./gradlew Http:makeJar

cd $out_path
du -b *.apk *.jar >> build-info.txt
echo "" >> build-info.txt
md5sum *.apk *.jar >> build-info.txt
git log -n 10 --stat >> git-log.txt

git_branch=`git symbolic-ref --short -q HEAD`
git_version=`git rev-list HEAD --count`
touch "${git_branch}-${git_version}"

echo ">>> Shell build apk end"