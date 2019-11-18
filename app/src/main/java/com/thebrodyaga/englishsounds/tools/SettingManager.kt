package com.thebrodyaga.englishsounds.tools

import android.content.SharedPreferences

class SettingManager constructor(
    private val sharedPreferences: SharedPreferences
) {

    fun setCurrentTheme(theme: CurrentTheme) {
        sharedPreferences.edit().putString(THEME_KEY, theme.name).apply()
    }


    fun getCurrentTheme(): CurrentTheme {
        val fromSp = sharedPreferences.getString(THEME_KEY, null)
            ?: return CurrentTheme.SYSTEM
        return if (CurrentTheme.values().map { it.name }.contains(fromSp))
            CurrentTheme.valueOf(fromSp)
        else CurrentTheme.SYSTEM
    }

    fun setLastVersionCode(versionCode: Int) {
        sharedPreferences.edit().putInt(VERSION_CODE_KEY, versionCode).apply()
    }


    fun getLastVersionCode(): Int {
        return sharedPreferences.getInt(VERSION_CODE_KEY, 0)
    }


    companion object {
        private const val THEME_KEY = "THEME_KEY"
        private const val VERSION_CODE_KEY = "VERSION_CODE_KEY"
    }

    enum class CurrentTheme {
        SYSTEM, DARK, LIGHT
    }
}