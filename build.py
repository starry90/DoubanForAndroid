#!/usr/bin/env python
# coding=utf-8

import os
import platform

current_path = os.path.abspath('.')
project_path = current_path + os.sep + "%s" + os.sep
out_path = project_path % 'outputs'

gradlew_clean = 'gradlew clean'
gradlew_build = 'gradlew assembleRelease'
if platform.system() == 'Windows':
    delete = r'del %s\*' % out_path
    copy_format = r'xcopy %s\build\outputs\* %s'
else:
    delete = r'rm -rf %s/*' % out_path
    copy_format = r'cp -r %s/build/outputs/* %s'
    gradlew_clean = './' + gradlew_clean
    gradlew_build = './' + gradlew_build

print out_path


def build_apk():
    print '--> build apk start'

    if not os.path.exists(out_path):  # outputs目录不存在
        os.makedirs(out_path)
    else:  # outputs目录存在,删除outputs下所有文件
        os.system(delete)

    os.system(gradlew_clean)
    build_code = os.system(gradlew_build)
    if build_code == 0:
        print '--> 打包成功'
        os.system(copy_format % ('app', out_path))
    else:
        '--> 打包失败'

    print '--> build apk end'


def main():
    build_apk()


if __name__ == '__main__':
    main()
