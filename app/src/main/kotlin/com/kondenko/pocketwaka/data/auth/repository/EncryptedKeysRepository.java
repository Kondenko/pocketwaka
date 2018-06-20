package com.kondenko.pocketwaka.data.auth.repository;

import io.reactivex.Single;

import static com.kondenko.pocketwaka.utils.FunctionsKt.report;

/**
 * Gets app ID and secret from native code
 * WARNING: If you change the location of this class,
 * make sure to reflect the changes in the native code file (keys.c)
 */
public class EncryptedKeysRepository {

    static {
        try {
            System.loadLibrary("keys");
        } catch (UnsatisfiedLinkError e) {
            report(e, "Couldn't load native library: keys");
        }
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
