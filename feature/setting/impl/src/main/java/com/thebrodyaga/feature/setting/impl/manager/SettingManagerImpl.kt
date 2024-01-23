package com.thebrodyaga.feature.setting.impl.manager

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.google.gson.Gson
import com.thebrodyaga.data.setting.api.CurrentTheme
import com.thebrodyaga.data.setting.api.SettingManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber
import javax.inject.Inject

class SettingManagerImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson,
) : SettingManager {

    private val isFirstAppStart: Boolean by lazy {
        val result = sharedPreferences.getBoolean(FIRST_APP_START_KEY, true)
        sharedPreferences.edit().putBoolean(FIRST_APP_START_KEY, false).apply()
        result
    }

    init {
        isFirstAppStart
    }

    private var appRateDto: AppRateDto = AppRateDto(0)
    private val needShowRate = MutableStateFlow(false)

    override fun setCurrentTheme(theme: CurrentTheme) {
        sharedPreferences.edit().putString(THEME_KEY, theme.name).apply()
    }

    override fun getCurrentTheme(): CurrentTheme {
        val fromSp = sharedPreferences.getString(THEME_KEY, null)
            ?: return CurrentTheme.SYSTEM
        return if (CurrentTheme.values().map { it.name }.contains(fromSp))
            CurrentTheme.valueOf(fromSp)
        else CurrentTheme.SYSTEM
    }

    override fun setLastVersionCode(versionCode: Int) {
        sharedPreferences.edit().putInt(VERSION_CODE_KEY, versionCode).apply()
    }

    override fun getLastVersionCode(): Int {
        return sharedPreferences.getInt(VERSION_CODE_KEY, 0)
    }

    private var showedRateDialog = false

    override fun needShowRateRequest(): StateFlow<Boolean> = needShowRate

    private fun innerNeedShowRateRequest(): Boolean {
        val rateDto = appRateDto
        val result = !showedRateDialog && rateDto.needShowRateRequest(isFirstAppStart)
        logRateLogic("needShowRateRequest", rateDto)
        Timber.i("needShowRateRequest result=$result")
        return result
    }

    override fun onRateRequestShow() {
        val rateDto = appRateDto
        showedRateDialog = true
        appRateDto = AppRateDto(0)
        logRateLogic("onRateRequestShow", rateDto)
    }

    override fun onSoundShowed() {
        val rateDto = appRateDto
        if (!showedRateDialog) {
            appRateDto = AppRateDto(rateDto.soundShowingCount.inc())
        }
        logRateLogic("onSoundShowed", rateDto)
        needShowRate.update { innerNeedShowRateRequest() }
    }

    override fun updateTheme() {
        AppCompatDelegate.setDefaultNightMode(
            when (getCurrentTheme()) {
                CurrentTheme.SYSTEM -> {
                    AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                }

                CurrentTheme.DARK -> {
                    AppCompatDelegate.MODE_NIGHT_YES
                }

                CurrentTheme.LIGHT -> {
                    AppCompatDelegate.MODE_NIGHT_NO
                }
            }
        )
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
}