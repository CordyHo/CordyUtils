package com.cordyho.utils

import androidx.preference.PreferenceManager

object PreferencesHelper {
    /**
     *
     * Activity.getPreferences(int mode)生成 Activity名.xml 用于Activity内部存储
     * PreferenceManager.getDefaultSharedPreferences(Context)生成 包名_preferences.xml
     * Context.getSharedPreferences(String name,int mode)生成name.xml
     */
    private val appPreference = PreferenceManager.getDefaultSharedPreferences(CordyUtils.application)

    fun setAppFlag(key: String, flag: Boolean) {
        appPreference
            .edit()
            .putBoolean(key, flag)
            .apply()
    }

    fun getAppFlag(key: String): Boolean {
        return appPreference
            .getBoolean(key, false)
    }

    fun removeAppFlag(key: String) {
        appPreference
            .edit()
            .remove(key)
            .apply()
    }

    fun addCustomAppProfile(key: String, s: String?) {
        appPreference
            .edit()
            .putString(key, s)
            .apply()
    }

    fun getCustomAppProfile(key: String): String? {
        return appPreference.getString(key, "")
    }
}