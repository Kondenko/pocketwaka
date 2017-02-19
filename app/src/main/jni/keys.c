#include <jni.h>

JNIEXPORT jstring JNICALL
Java_com_kondenko_pocketwaka_api_KeysManager_getAppId(JNIEnv *env, jclass clazz) {
    return (*env) ->  NewStringUTF(env, "YidCU2RpemVHdHlhZ3JGWUc5cmg1aWw5cDEn");
}

JNIEXPORT jstring JNICALL
Java_com_kondenko_pocketwaka_api_KeysManager_getAppSecret(JNIEnv *env, jclass clazz) {
    return (*env) -> NewStringUTF(env, "c2VjX2InaUVid0RlSkVZcHpGQnY2MkxfcGhic1JVelMzNjFIN1RicWI2dHpwOXZocjB6cVllTHJvS29RQW5kSlcySVRtNVRRcTdfV2VSMlYzQ3ZGVVMn");
}