package com.thebrodyaga.englishsounds.tools

import android.content.SharedPreferences
import com.google.gson.Gson
import com.thebrodyaga.englishsounds.domine.entities.data.AppRateDto
import timber.log.Timber

class SettingManager constructor(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) {

    private val isFirstAppStart: Boolean by lazy {
        val result = sharedPreferences.getBoolean(FIRST_APP_START_KEY, true)
        sharedPreferences.edit().putBoolean(FIRST_APP_START_KEY, false).apply()
        result
    }

    init {
        isFirstAppStart
    }

    private var appRateDto: AppRateDto = AppRateDto(0)

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

    private var showedRateDialog = false

    fun needShowRateRequest(): Boolean {
        val rateDto = appRateDto
        val result = !showedRateDialog && rateDto.needShowRateRequest(isFirstAppStart)
        logRateLogic("needShowRateRequest", rateDto)
        Timber.i("needShowRateRequest result=$result")
        return result
    }

    fun onRateRequestShow() {
        val rateDto = appRateDto
        showedRateDialog = true
        appRateDto = AppRateDto(0)
        logRateLogic("onRateRequestShow", rateDto)
    }

    fun onSoundShowed() {
        val rateDto = appRateDto
        if (!showedRateDialog) {
            appRateDto = AppRateDto(rateDto.soundShowingCount.inc())
        }
        logRateLogic("onSoundShowed", rateDto)
    }

    private fun logRateLogic(method: String, appRateDto: AppRateDto) {
        Timber.i("$method soundShowingCount=${appRateDto.soundShowingCount}")
    }

    companion object {
        private const val THEME_KEY = "THEME_KEY"
        private const val VERSION_CODE_KEY = "VERSION_CODE_KEY"
        private const val APP_RATE_KEY = "APP_RATE_KEY"
        private const val FIRST_APP_START_KEY = "FIRST_APP_START_KEY"
    }

    enum class CurrentTheme {
        SYSTEM, DARK, LIGHT
    }
}