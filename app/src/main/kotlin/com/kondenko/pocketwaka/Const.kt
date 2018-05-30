package com.kondenko.pocketwaka

// TODO Remove top Const object
object Const {

    const val DEFAULT_ANIM_DURATION: Long = 400

    /** URLs */
    const val URL_PLUGINS = "https://wakatime.com/editors"

    const val BASE_URL = "https://wakatime.com/" // Used when a user is not authenticated yet
    const val URL_AUTH = "${BASE_URL}oauth/authorize" // Used to authenticate a user
    const val URL_API = "${BASE_URL}/api/v1/" // Used for all API calls

    /** Authorization */
    const val AUTH_REDIRECT_URI = "pocketwaka://oauth2"


    const val HEADER_ACCEPT_NAME = "Accept"
    const val HEADER_ACCEPT_VALUE = "application/x-www-form-urlencode"

    const val HEADER_ACCEPT = HEADER_ACCEPT_NAME + " : " + HEADER_ACCEPT_VALUE

    const val HEADER_BEARER_NAME = "Authorization"

    const val RESPONSE_TYPE_CODE = "code"

    /** Stats */

    // Ranges
    const val STATS_RANGE_KEY = "stats_range"
    const val STATS_RANGE_7_DAYS = "last_7_days"
    const val STATS_RANGE_30_DAYS = "last_30_days"
    const val STATS_RANGE_6_MONTHS = "last_6_months"
    const val STATS_RANGE_1_YEAR = "last_year"

    const val MAX_SHADOW_OPACITY = .2f

}