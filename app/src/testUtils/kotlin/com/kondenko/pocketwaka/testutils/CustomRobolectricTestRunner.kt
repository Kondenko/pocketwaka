package com.kondenko.pocketwaka.testutils

import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

class CustomRobolectricTestRunner(clazz: Class<Any?>) : RobolectricTestRunner(clazz) {

    override fun buildGlobalConfig(): Config = Config.Builder.defaults()
            .setManifest("app/src/main/AndroidManifest.xml")
            .build()

}