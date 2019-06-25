package com.kondenko.pocketwaka.screens.stats

class TabsElevationHelper {

    private var elevationStates = mutableMapOf<Int, Boolean>()

    var currentTabIndex = 0

    var isElevated: Boolean
        get() = elevationStates[currentTabIndex] ?: false
        set(value) {
            elevationStates[currentTabIndex] = value
        }


}