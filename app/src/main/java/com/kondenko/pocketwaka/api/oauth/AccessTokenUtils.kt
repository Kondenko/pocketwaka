package com.kondenko.pocketwaka.api.oauth

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

object AccessTokenUtils {

    private val KEY_ACCESS_TOKEN = "access_token"
    private val KEY_EXPIRES_IN = "expires_in"
    private val KEY_REFRESH_TOKEN = "refresh_token"
    private val KEY_SCOPE = "scope"
    private val KEY_TOKEN_TYPE = "token_type"
    private val KEY_UID = "uid"

    fun getFromPreferences(context: Context): AccessToken {
        val sp = getPrefs(context)
        if (!isTokenSaved(sp)) throw IllegalStateException("Access Token not yet acquired")
        return AccessToken(
                sp.getString(KEY_ACCESS_TOKEN, null),
                sp.getFloat(KEY_EXPIRES_IN, 0f).toDouble(),
                sp.getString(KEY_REFRESH_TOKEN, null),
                sp.getString(KEY_SCOPE, null),
                sp.getString(KEY_TOKEN_TYPE, null),
                sp.getString(KEY_UID, null)
        )
    }

    fun getToken(context: Context): String {
        return getPrefs(context).getString(KEY_ACCESS_TOKEN, null)
    }

    fun storeToPreferences(token: AccessToken, context: Context) {
        val sp = getPrefs(context)
        val e = sp.edit()
        e.putString(KEY_ACCESS_TOKEN, token.access_token)
        e.putFloat(KEY_EXPIRES_IN, token.expires_in.toFloat())
        e.putString(KEY_REFRESH_TOKEN, token.refresh_token)
        e.putString(KEY_SCOPE, token.scope)
        e.putString(KEY_TOKEN_TYPE, token.token_type)
        e.putString(KEY_UID, token.uid)
        e.apply()
    }

    fun isTokenSaved(context: Context): Boolean {
        return getPrefs(context).contains(KEY_ACCESS_TOKEN)
    }

    fun isTokenSaved(prefs: SharedPreferences): Boolean {
        return prefs.contains(KEY_ACCESS_TOKEN)
    }

    private fun getPrefs(context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

}