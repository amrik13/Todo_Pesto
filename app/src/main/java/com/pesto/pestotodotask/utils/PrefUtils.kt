package com.pesto.pestotodotask.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.core.content.edit

object PrefUtils {

    const val IS_USER_LOGGED_IN = "IS_USER_LOGGED_IN"
    const val USER_ID = "USER_ID"

    private fun put(context: Context, key: String, value: Any) {
        val sharedPref = context.getSharedPreferences("PestoSharedPref", MODE_PRIVATE)
        sharedPref.edit {
            when (value) {
                is Int -> putInt(key, value)
                is String -> putString(key, value)
                is Boolean -> putBoolean(key, value)
                is Long -> putLong(key, value)
            }
            apply()
            commit()
        }
    }

    fun isUserLoggedIn(context: Context): Boolean {
        val sharedPref = context.getSharedPreferences("PestoSharedPref", MODE_PRIVATE)
        return sharedPref.getBoolean(IS_USER_LOGGED_IN, false)
    }

    fun setUserLoggedIn(context: Context, isLoggedIn: Boolean) {
        put(context, IS_USER_LOGGED_IN, isLoggedIn)
    }

    fun getUserId(context: Context): String? {
        val sharedPref = context.getSharedPreferences("PestoSharedPref", MODE_PRIVATE)
        return sharedPref.getString(USER_ID, "")
    }

    fun setUserId(context: Context, userId: String) {
        Constant.USER_ID = userId
        put(context, USER_ID, userId)
    }



}