package com.parithidb.zobazeassignment.util

import android.content.Context

class SharedPrefHelper(
    context: Context
) {
    private val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

    private val USER_NAME = "user_name"
    private val USER_EMAIL = "user_email"
    private val FCM_TOKEN = "fcm_token"
    private val MOCK_DATA_INSERTED = "mock_data_inserted"

    fun setUserName(name: String?) {
        sharedPreferences.edit().putString(USER_NAME, name).apply()
    }

    fun getUserName(): String? {
        return sharedPreferences.getString(USER_NAME, null)
    }

    fun setUserEmail(name: String?) {
        sharedPreferences.edit().putString(USER_EMAIL, name).apply()
    }

    fun getUserEmail(): String? {
        return sharedPreferences.getString(USER_EMAIL, null)
    }

    fun setFCMToken(name: String?) {
        sharedPreferences.edit().putString(FCM_TOKEN, name).apply()
    }

    fun getFCMToken(): String? {
        return sharedPreferences.getString(FCM_TOKEN, null)
    }

    fun clearPreferences() {
        sharedPreferences.edit().clear().apply()
    }

    fun hasInsertedMockData(): Boolean {
        return sharedPreferences.getBoolean(MOCK_DATA_INSERTED, false)
    }

    fun setInsertedMockData() {
        sharedPreferences.edit().putBoolean(MOCK_DATA_INSERTED, true).apply()
    }

}