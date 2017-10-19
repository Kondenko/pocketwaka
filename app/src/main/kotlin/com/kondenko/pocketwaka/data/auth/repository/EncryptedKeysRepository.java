package com.kondenko.pocketwaka.data.auth.repository;

import io.reactivex.Single;

/**
 * Gets app ID and secret from native code
 * WARNING: If you change the location of this class,
 * make sure to reflect the changes in the native code file (keys.c)
 */
public class EncryptedKeysRepository {

    static {
        System.loadLibrary("keys");
    }

    private static native String getAppIdEncrypted();

    private static native String getAppSecretEncrypted();

    public Single<String> getAppId() {
        return Single.just(getAppIdEncrypted());
    }

    public Single<String> getAppSecret() {
        return Single.just(getAppSecretEncrypted());
    }

}
