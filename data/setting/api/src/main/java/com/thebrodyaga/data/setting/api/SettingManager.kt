package com.thebrodyaga.data.setting.api

import kotlinx.coroutines.flow.Flow

interface SettingManager {
    fun setCurrentTheme(theme: CurrentTheme)
    fun getCurrentTheme(): CurrentTheme
    fun setLastVersionCode(versionCode: Int)
    fun getLastVersionCode(): Int
    fun needShowRateRequest(): Flow<Boolean>
    fun onRateRequestShow()
    fun onSoundShowed()
    fun updateTheme()
    fun isFirstAppStart(): Boolean
}

enum class CurrentTheme {
    SYSTEM, DARK, LIGHT
}