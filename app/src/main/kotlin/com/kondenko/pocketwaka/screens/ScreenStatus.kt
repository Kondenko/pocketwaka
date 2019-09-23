package com.kondenko.pocketwaka.screens

interface StatusMarker {
    val status: ScreenStatus
}

sealed class ScreenStatus(val lastUpdated: Long? = null) {
    class Loading(lastUpdated: Long? = null) : ScreenStatus(lastUpdated)
    class Offline(lastUpdated: Long? = null) : ScreenStatus(lastUpdated)
}
