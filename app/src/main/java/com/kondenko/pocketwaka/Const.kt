package com.kondenko.pocketwaka

object Const {

    // URLs
    const val URL_PLUGINS = "https://wakatime.com/editors"

    const val BASE_URL = "https://wakatime.com/"
    const val API_URL = "https://wakatime.com/api/v1/"

    const val AUTH_URL_POSTFIX = "oauth/authorize"
    const val TOKEN_URL_POSTFIX = "oauth/token"

    const val AUTH_URL = BASE_URL + AUTH_URL_POSTFIX

    // Authorization
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

    const val STATS_RANGE_KEY = "stats_range"
    const val STATS_RANGE_7_DAYS = "last_7_days"
    const val STATS_RANGE_30_DAYS = "last_30_days"
    const val STATS_RANGE_6_MONTHS = "last_6_months"
    const val STATS_RANGE_1_YEAR = "last_year"

    const val MAX_SHADOW_OPACITY = .2f
    const val DEFAULT_ANIM_DURATION: Long = 400

}