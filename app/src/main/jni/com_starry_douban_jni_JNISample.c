//
// Created by Starry Jerry on 2017/3/28.
//

#include "com_starry_douban_jni_JNISample.h"

JNIEXPORT jstring JNICALL Java_com_starry_douban_jni_JNISample_test (JNIEnv *env, jobject obj){

    return (*env) -> NewStringUTF(env,"我来自JNI");
}