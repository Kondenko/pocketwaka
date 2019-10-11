package com.kondenko.pocketwaka.domain.menu

data class MenuUiModel(
        val githubUrl: String?,
        val supportEmail: String?,
        val emailSubject: String,
        val initialEmailText: String
)

data class DeviceInfo(
        val deviceName: String,
        val osVersion: String,
        val appVersion: String
)