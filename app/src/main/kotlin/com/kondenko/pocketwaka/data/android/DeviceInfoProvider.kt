package com.kondenko.pocketwaka.data.android

import android.os.Build
import com.kondenko.pocketwaka.BuildConfig
import com.kondenko.pocketwaka.domain.menu.DeviceInfo

class DeviceInfoProvider {

    fun getDeviceInfo() = DeviceInfo(
            deviceName = "${Build.MODEL} (${Build.DEVICE})",
            osVersion = "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})",
            appVersion = "${BuildConfig.APPLICATION_ID} ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
    )

}