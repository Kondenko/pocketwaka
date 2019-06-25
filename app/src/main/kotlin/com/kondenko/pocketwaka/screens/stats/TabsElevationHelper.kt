package com.kondenko.pocketwaka.screens.stats

import timber.log.Timber

class TabsElevationHelper {

    private var elevationStates = mutableMapOf<Int, Boolean>()

    var currentTabIndex = 0
        set(value) {
            field = value
            Timber.d("Tab $value selected")
        }

    var isElevated: Boolean
        get() = elevationStates[currentTabIndex] ?: false
        set(value) {
            Timber.d("Tab $currentTabIndex elevated: $value")
            elevationStates[currentTabIndex] = value
        }


}