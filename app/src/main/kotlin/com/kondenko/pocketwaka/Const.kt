package com.kondenko.pocketwaka

object Const {

    const val DEFAULT_ANIM_DURATION: Long = 400

    /** URLs */
    const val URL_PLUGINS = "https://wakatime.com/editors"

    // Auth
    const val URL_TYPE_AUTH = "base_url"
    const val URL_OAUTH = "https://wakatime.com/" // Used when a user is not authenticated yet
    const val TOKEN_URL_POSTFIX = "oauth/token" // Used in a POST request to obtain a token
    const val URL_AUTH = URL_OAUTH + "oauth/authorize" // Used to authenticate a user
    // API
    const val URL_API = "https://wakatime.com/api/v1/" // Used for all API calls

    /** Authorization */
    const val URL_TYPE_API = "api_url"
    const val AUTH_REDIRECT_URI = "pocketwaka://oauth2"


    const val HEADER_ACCEPT_NAME = "Accept"
    const val HEADER_ACCEPT_VALUE = "application/x-www-form-urlencode"

    const val HEADER_ACCEPT = HEADER_ACCEPT_NAME + " : " + HEADER_ACCEPT_VALUE

    const val HEADER_BEARER_NAME = "Authorization"
    const val HEADER_BEARER_VALUE_PREFIX = "Bearer"

    const val GRANT_TYPE_AUTH_CODE = "authorization_code"
    const val RESPONSE_TYPE_CODE = "code"

    // Scopes
    const val SCOPE_EMAIL = "email"
    const val SCOPE_READ_LOGGED_TIME = "read_logged_time"
    const val SCOPE_WRITE_LOGGED_TIME = "write_logged_time"
    const val SCOPE_READ_STATS = "read_stats"
    const val SCOPE_READ_TEAMS = "read_teams"

    /** Stats */

    // Ranges
    const val STATS_RANGE_KEY = "stats_range"
    const val STATS_RANGE_7_DAYS = "last_7_days"
    const val STATS_RANGE_30_DAYS = "last_30_days"
    const val STATS_RANGE_6_MONTHS = "last_6_months"
    const val STATS_RANGE_1_YEAR = "last_year"

    const val MAX_SHADOW_OPACITY = .2f

}