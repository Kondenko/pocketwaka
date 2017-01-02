package com.kondenko.pocketwaka.api;

public class KeysManager {

    static {
        System.loadLibrary("keys");
    }

    public static native String getAppId();
    public static native String getAppSecret();

}
