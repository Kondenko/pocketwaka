package com.kondenko.pocketwaka.data

import android.content.SharedPreferences

class SharedPreferencesRepository(private val sharedPreferences: SharedPreferences) {

    private companion object Keys {
        private const val KEY_HAS_SEEN_SUMMARY_ONBOARDING = "has_seen_summary_onboarding"
    }

    var hasSeenSummaryOnboarding: Boolean = false

/*
    STOPSHIP // TODO Uncomment
    var hasSeenSummaryOnboarding: Boolean
        get() = sharedPreferences.getBoolean(KEY_HAS_SEEN_SUMMARY_ONBOARDING, false)
        set(value) = sharedPreferences.edit {
            putBoolean(KEY_HAS_SEEN_SUMMARY_ONBOARDING, value)
        }
*/

}