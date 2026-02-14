package com.example.animebrowser

import android.content.Context
import android.content.SharedPreferences

object UserSession {
    private const val PREF_NAME = "AnimeBrowserPrefs"
    private const val KEY_IS_LOGGED_IN = "isLoggedIn"
    private const val KEY_IS_GUEST = "isGuest"
    private const val KEY_USER_EMAIL = "userEmail"
    private const val KEY_USERNAME = "username"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun login(context: Context, email: String, username: String) {
        getPrefs(context).edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, true)
            putBoolean(KEY_IS_GUEST, false)
            putString(KEY_USER_EMAIL, email)
            putString(KEY_USERNAME, username)
            apply()
        }
    }

    fun setGuestMode(context: Context, isGuest: Boolean) {
        getPrefs(context).edit().apply {
            putBoolean(KEY_IS_GUEST, isGuest)
            putBoolean(KEY_IS_LOGGED_IN, false)
            apply()
        }
    }

    fun logout(context: Context) {
        getPrefs(context).edit().clear().apply()
    }

    fun isLoggedIn(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun isGuest(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_IS_GUEST, false)
    }

    fun getUsername(context: Context): String {
        return getPrefs(context).getString(KEY_USERNAME, "User") ?: "User"
    }

    fun getEmail(context: Context): String {
        return getPrefs(context).getString(KEY_USER_EMAIL, "") ?: ""
    }
}