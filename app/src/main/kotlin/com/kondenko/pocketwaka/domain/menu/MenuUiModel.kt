package com.kondenko.pocketwaka.domain.menu

data class MenuUiModel(
    val githubUrl: String,
    val privacyPolicyUrl: String,
    val supportEmail: String,
    val emailSubject: String,
    val initialEmailText: String,
    val positiveRatingThreshold: Int
)

data class DeviceInfo(
        val deviceName: String,
        val osVersion: String,
        val appVersion: String
)