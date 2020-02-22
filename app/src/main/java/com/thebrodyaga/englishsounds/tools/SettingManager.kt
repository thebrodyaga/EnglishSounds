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

    private var appRateDto: AppRateDto
        set(value) = sharedPreferences.edit().putString(APP_RATE_KEY, gson.toJson(value)).apply()
        get() = try {
            gson.fromJson(sharedPreferences.getString(APP_RATE_KEY, ""), AppRateDto::class.java)
        } catch (e: Exception) {
            AppRateDto()
        }

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
        val result = !isFirstAppStart && !showedRateDialog && rateDto.needShowRateRequest()
        logRateLogic("needShowRateRequest", rateDto)
        Timber.i("needShowRateRequest result=$result")
        return result
    }

    fun onLaterRate() {
        val rateDto = appRateDto
        appRateDto = AppRateDto(0, rateDto.lessFourRateCount, false)
        logRateLogic("onLaterRate", rateDto)
    }

    fun onRated() {
        val rateDto = appRateDto
        appRateDto = AppRateDto(rateDto.soundShowingCount, rateDto.lessFourRateCount, true)
        logRateLogic("onRated", rateDto)
    }

    fun onRateLessThenFour() {
        val rateDto = appRateDto
        val inc = rateDto.lessFourRateCount.inc()
        appRateDto = AppRateDto(0, inc, inc >= AppRateDto.MAX_RATE_TRY)
        logRateLogic("onRateLessThenFour", rateDto)
    }

    fun onRateRequestShow() {
        val rateDto = appRateDto
        showedRateDialog = true
        appRateDto = AppRateDto(
                when (rateDto.lessFourRateCount) {
                    0 -> AppRateDto.SOUND_SHOW_COUNT_BEFORE_RATE / 2
                    1 -> AppRateDto.SOUND_SHOW_COUNT_BEFORE_RATE_SECOND_TRY / 2
                    else -> 0
                }, rateDto.lessFourRateCount, rateDto.rated
        )
        logRateLogic("onRateRequestShow", rateDto)
    }

    fun onSoundShowed() {
        val rateDto = appRateDto
        if (!isFirstAppStart && !rateDto.rated && !showedRateDialog) {
            appRateDto = AppRateDto(
                    rateDto.soundShowingCount.inc(),
                    rateDto.lessFourRateCount,
                    rateDto.rated
            )
        }
        logRateLogic("onSoundShowed", rateDto)
    }

    private fun logRateLogic(method: String, appRateDto: AppRateDto) {
        Timber.i("$method soundShowingCount=${appRateDto.soundShowingCount} lessFourRateCount=${appRateDto.lessFourRateCount} rated=${appRateDto.rated} ")
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