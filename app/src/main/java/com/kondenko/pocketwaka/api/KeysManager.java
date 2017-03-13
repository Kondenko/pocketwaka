package com.kondenko.pocketwaka.api;

import com.kondenko.pocketwaka.utils.Encryptor;

/**
 * Gets app ID and secret from native code
 * WARNING: If you change the location of this class,
 * make sure to reflect the changes in native code file (keys.c)
 */
public class KeysManager {

    static {
        System.loadLibrary("keys");
    }

    private static native String getAppIdEncrypted();
    private static native String getAppSecretEncrypted();

    public static String getAppId() {
        return Encryptor.decrypt(getAppIdEncrypted());
    }

    public static String getAppSecret() {
        return Encryptor.decrypt(getAppSecretEncrypted());
    }
}
