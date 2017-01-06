package com.kondenko.pocketwaka.api.oauth

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.kondenko.pocketwaka.Const

object AccessTokenUtils {

    private val KEY_ACCESS_TOKEN = "access_token"
    private val KEY_EXPIRES_IN = "expires_in"
    private val KEY_REFRESH_TOKEN = "refresh_token"
    private val KEY_SCOPE = "scope"
    private val KEY_TOKEN_TYPE = "token_type"
    private val KEY_UID = "uid"

    fun getTokenObject(context: Context): AccessToken {
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

    fun getTokenString(context: Context): String {
        return getPrefs(context).getString(KEY_ACCESS_TOKEN, null)
    }

    fun getTokenHeaderValue(context: Context): String {
        return Const.HEADER_BEARER_VALUE_PREFIX + " " + getTokenString(context)
    }

    fun storeToPreferences(token: AccessToken, context: Context) {
        val editor = getPrefs(context).edit()
        with(editor) {
            putString(KEY_ACCESS_TOKEN, token.access_token)
            putFloat(KEY_EXPIRES_IN, token.expires_in.toFloat())
            putString(KEY_REFRESH_TOKEN, token.refresh_token)
            putString(KEY_SCOPE, token.scope)
            putString(KEY_TOKEN_TYPE, token.token_type)
            putString(KEY_UID, token.uid)
            apply()
        }
    }

    fun isTokenSaved(context: Context): Boolean {
        return getPrefs(context).contains(KEY_ACCESS_TOKEN)
    }

    fun isTokenSaved(prefs: SharedPreferences): Boolean {
        return prefs.contains(KEY_ACCESS_TOKEN)
    }

    fun removeFromPrefs(context: Context) {
        val e = getPrefs(context).edit()
        with(e) {
            remove(KEY_ACCESS_TOKEN)
            remove(KEY_EXPIRES_IN)
            remove(KEY_REFRESH_TOKEN)
            remove(KEY_SCOPE)
            remove(KEY_TOKEN_TYPE)
            remove(KEY_UID)
            apply()
        }

    }

    private fun getPrefs(context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

}