#!/usr/bin/env python
# coding=utf-8

import os
import platform
import shutil

current_path = os.path.abspath('.')
out_path = os.path.join(current_path, 'outputs')
build_path = os.path.join(current_path, 'app', 'build', 'outputs')
apk_path = os.path.join(build_path, 'apk')
mapping_path = os.path.join(build_path, 'mapping', 'release')

gradlew_clean = 'gradlew clean'
gradlew_build = 'gradlew assemble'
if platform.system() != 'Windows':
    gradlew_clean = './' + gradlew_clean
    gradlew_build = './' + gradlew_build

print out_path


def build_apk():
    print '>>> Python build apk start'

    if os.path.exists(out_path):  # outputs目录存在,删除outputs下所有文件
        shutil.rmtree(out_path)  # 删除文件夹
    os.makedirs(out_path)  # 创建文件夹

    os.system(gradlew_clean)
    build_code = os.system(gradlew_build)
    if build_code == 0:
        # copy mapping.txt
        shutil.copy(os.path.join(mapping_path, 'mapping.txt'), out_path)
        # copy *.apk
        files = os.listdir(apk_path)
        for file_temp in files:
            print file_temp
            shutil.copy(os.path.join(apk_path, file_temp), out_path)

    print '>>> Python build ', build_code == 0
    print '>>> Python build apk end'


def main():
    build_apk()


if __name__ == '__main__':
    main()
