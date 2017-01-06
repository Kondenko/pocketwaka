package com.kondenko.pocketwaka.api;

/**
 * Gets app ID and secret from native code
 * WARNING: If you change the location of this class,
 * make sure to reflect the changes in native code file (keys.c)
 */
public class KeysManager {

    static {
        System.loadLibrary("keys");
    }

    public static native String getAppId();
    public static native String getAppSecret();

}
