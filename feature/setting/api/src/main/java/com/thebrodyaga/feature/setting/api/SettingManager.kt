package com.thebrodyaga.feature.setting.api

interface SettingManager {
    fun setCurrentTheme(theme: CurrentTheme)
    fun getCurrentTheme(): CurrentTheme
    fun setLastVersionCode(versionCode: Int)
    fun getLastVersionCode(): Int
    fun needShowRateRequest(): Boolean
    fun onRateRequestShow()
    fun onSoundShowed()
    fun updateTheme()
}

enum class CurrentTheme {
    SYSTEM, DARK, LIGHT
}