package com.thebrodyaga.data.sounds.api

interface SettingManager {
    fun setCurrentTheme(theme: CurrentTheme)
    fun getCurrentTheme(): CurrentTheme
    fun setLastVersionCode(versionCode: Int)
    fun getLastVersionCode(): Int
    fun needShowRateRequest(): Boolean
    fun onRateRequestShow()
    fun onSoundShowed()
}

enum class CurrentTheme {
    SYSTEM, DARK, LIGHT
}