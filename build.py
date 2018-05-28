#!/usr/bin/env python
# coding=utf-8

import hashlib
import os
import platform
import shutil

current_path = os.path.abspath('.')
out_path = os.path.join(current_path, 'outputs')
build_path = os.path.join(current_path, 'app', 'build', 'outputs')

gradlew_clean = 'gradlew clean'
gradlew_build = 'gradlew assemble'
if platform.system() != 'Windows':
    gradlew_clean = './' + gradlew_clean
    gradlew_build = './' + gradlew_build

print out_path


def copy_file_from_dir(dir_path):
    for i in os.listdir(dir_path):
        i_path = os.path.join(dir_path, i)
        if os.path.isfile(i_path):
            print i_path
            if 'mapping.txt' in i or 'apk' in i:
                shutil.copy(i_path, out_path)
        else:
            copy_file_from_dir(i_path)


def file_md5_from_dif(dir_path):
    build_info = os.path.join(out_path, 'build-info.txt')
    for i in os.listdir(dir_path):
        i_path = os.path.join(dir_path, i)
        if os.path.isfile(i_path) and 'apk' in i:
            with open(i_path, 'rb') as f:
                md5 = hashlib.md5(f.read()).hexdigest()
                print md5, i
                with open(build_info, 'a+') as build_file:
                    build_file.writelines('%s %s\n' % (md5, i))


def build_apk():
    """
    ├─app
    │  ├─build
    │  │  ├─outputs
    │  │  │  ├─apk
    │  │  │  │  ├─debug
    │  │  │  │  └─release
    │  │  │  ├─logs
    │  │  │  └─mapping
    │  │  │      └─release
    """

    print '>>> Python build apk start'

    if os.path.exists(out_path):  # outputs目录存在,删除outputs下所有文件
        shutil.rmtree(out_path)  # 删除文件夹
    os.makedirs(out_path)  # 创建文件夹

    os.system(gradlew_clean)
    build_code = os.system(gradlew_build)
    if build_code == 0:
        # copy *.apk and mapping.txt
        copy_file_from_dir(build_path)
        # get apk file md5
        file_md5_from_dif(out_path)

    print '>>> Python build ', build_code == 0
    print '>>> Python build apk end'


def main():
    build_apk()


if __name__ == '__main__':
    main()
