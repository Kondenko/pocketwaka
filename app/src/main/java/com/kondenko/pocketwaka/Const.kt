package com.kondenko.pocketwaka

object Const {

    // URLs
    const val BASE_URL = "https://wakatime.com/"

    const val API_BASE_URL_POSTFIX = "api/v1/"
    const val AUTH_URL_POSTFIX = "oauth/authorize"
    const val TOKEN_URL_POSTFIX = "oauth/token"

    const val AUTH_URL = BASE_URL + AUTH_URL_POSTFIX

    // Authorization
    const val AUTH_REDIRECT_URI = "pocketwaka://oauth2"

    const val HEADER_ACCEPT = "Accept: application/x-www-form-urlencode"

    const val GRANT_TYPE_AUTH_CODE = "authorization_code"
    const val RESPONSE_TYPE_CODE = "code"

    // Scopes
    const val SCOPE_EMAIL = "email"
    const val SCOPE_READ_LOGGED_TIME = "read_logged_time"
    const val SCOPE_WRITE_LOGGED_TIME = "write_logged_time"
    const val SCOPE_READ_STATS = "read_stats"
    const val SCOPE_READ_TEAMS = "read_teams"

}