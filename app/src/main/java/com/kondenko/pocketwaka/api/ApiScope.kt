package com.kondenko.pocketwaka.api

import com.kondenko.pocketwaka.Const

enum class ApiScope(val s: String) {
    EMAIL(Const.SCOPE_EMAIL),
    READ_LOGGED_TIME(Const.SCOPE_READ_LOGGED_TIME),
    WRITE_LOGGED_TIME(Const.SCOPE_WRITE_LOGGED_TIME),
    READ_STATS(Const.SCOPE_READ_STATS),
    READ_TEAMS(Const.SCOPE_READ_TEAMS)
}