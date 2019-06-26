package com.kondenko.pocketwaka.screens.stats.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TabsElevationState(
        private val elevationStates: MutableMap<Int, Boolean> = mutableMapOf(),
        var currentTabIndex: Int = 0
) : Parcelable {

    var isElevated: Boolean
        get() = elevationStates[currentTabIndex] ?: false
        set(value) {
            elevationStates[currentTabIndex] = value
        }

}