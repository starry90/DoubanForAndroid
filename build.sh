#########################################################################
# File Name: build.sh
# 注意:jenkins上用的是dash不是bash 语法上有区别!!!!
#########################################################################
#!/bin/sh
set -u
set -e

#项目根目录
project_path=`pwd`
#最终输出目录
out_path=$project_path/outputs/

buildApk()
{
    echo ">> 编译开始"

    if [ ! -d $out_path ]; then
        mkdir $out_path
    else
        cd $out_path
        rm -rf *.apk
    fi

    cd $project_path
    #增加执行权限
    chmod +x gradlew
    #clean
    ./gradlew clean
    #打包Release
    ./gradlew assembleRelease
    #-r表示递归处理，将指定目录下的文件与子目录一并处理。
    #若源文件或目录的形态，不属于目录或符号链接，则一律视为普通文件处理
    echo "cp -r app/build/outputs/* to "$out_path
    cp -r app/build/outputs/* $out_path
    echo "<< 编译成功"
    exit
}

buildApk