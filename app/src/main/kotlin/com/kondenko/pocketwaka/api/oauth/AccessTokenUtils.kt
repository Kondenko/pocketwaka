package com.kondenko.pocketwaka.api.oauth

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import com.kondenko.pocketwaka.Const
import com.kondenko.pocketwaka.utils.Encryptor

object AccessTokenUtils {

    private val KEY_ACCESS_TOKEN = "access_token"
    private val KEY_EXPIRES_IN = "expires_in"
    private val KEY_REFRESH_TOKEN = "refresh_token"
    private val KEY_SCOPE = "scope"
    private val KEY_TOKEN_TYPE = "token_type"
    private val KEY_UID = "uid"
    private val KEY_CREATED_AT = "created_at"

    fun getTokenObject(context: Context): AccessToken {
        val sp = getPrefs(context)
        if (!isTokenSaved(sp)) throw IllegalStateException("Access Token not yet acquired")
        val encryptedToken = AccessToken(
                sp.getString(KEY_ACCESS_TOKEN, null),
                sp.getFloat(KEY_EXPIRES_IN, 0f).toDouble(),
                sp.getString(KEY_REFRESH_TOKEN, null),
                sp.getString(KEY_SCOPE, null),
                sp.getString(KEY_TOKEN_TYPE, null),
                sp.getString(KEY_UID, null),
                sp.getFloat(KEY_CREATED_AT, 0f)
        )
        return decryptToken(encryptedToken)
    }

    fun getTokenHeaderValue(context: Context): String {
        return Const.HEADER_BEARER_VALUE_PREFIX + " " + getAccessToken(context)
    }

    fun saveToken(token: AccessToken, context: Context) {
        val encryptedToken = encryptToken(token)
        val editor = getPrefs(context).edit()
        with(editor) {
            putString(KEY_ACCESS_TOKEN, encryptedToken.access_token)
            putFloat(KEY_EXPIRES_IN, encryptedToken.expires_in.toFloat())
            putString(KEY_REFRESH_TOKEN, encryptedToken.refresh_token)
            putString(KEY_SCOPE, encryptedToken.scope)
            putString(KEY_TOKEN_TYPE, encryptedToken.token_type)
            putString(KEY_UID, encryptedToken.uid)
            putFloat(KEY_CREATED_AT, encryptedToken.created_at)
            apply()
        }
    }

    fun deleteToken(context: Context) {
        val e = getPrefs(context).edit()
        with(e) {
            remove(KEY_ACCESS_TOKEN)
            remove(KEY_EXPIRES_IN)
            remove(KEY_REFRESH_TOKEN)
            remove(KEY_SCOPE)
            remove(KEY_TOKEN_TYPE)
            remove(KEY_UID)
            remove(KEY_CREATED_AT)
            apply()
        }

    }

    fun isTokenSaved(context: Context): Boolean {
        return getPrefs(context).contains(KEY_ACCESS_TOKEN)
    }

    fun isTokenSaved(prefs: SharedPreferences): Boolean {
        return prefs.contains(KEY_ACCESS_TOKEN)
    }

    private fun getAccessToken(context: Context): String {
        return Encryptor.decrypt(getPrefs(context).getString(KEY_ACCESS_TOKEN, null))
    }

    private fun encryptToken(token: AccessToken): AccessToken {
        val decryptedToken = token
        token.access_token = Encryptor.encrypt(token.access_token)
        token.refresh_token = Encryptor.encrypt(token.refresh_token)
        token.scope = Encryptor.encrypt(token.scope)
        token.token_type = Encryptor.encrypt(token.token_type)
        token.uid = Encryptor.encrypt(token.uid)
        return decryptedToken
    }

    private fun decryptToken(token: AccessToken): AccessToken {
        val decryptedToken = token
        token.access_token = Encryptor.decrypt(token.access_token)
        token.refresh_token = Encryptor.decrypt(token.refresh_token)
        token.scope = Encryptor.decrypt(token.scope)
        token.token_type = Encryptor.decrypt(token.token_type)
        token.uid = Encryptor.decrypt(token.uid)
        return decryptedToken
    }

    private fun getPrefs(context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

}