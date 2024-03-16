package com.thebrodyaga.feature.setting.impl.manager

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.google.gson.Gson
import com.thebrodyaga.data.setting.api.CurrentTheme
import com.thebrodyaga.data.setting.api.SettingManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import timber.log.Timber
import javax.inject.Inject

class SettingManagerImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson,
) : SettingManager {

    private val internalIsFirstAppStart: Boolean by lazy {
        val result = sharedPreferences.getBoolean(FIRST_APP_START_KEY, true)
        sharedPreferences.edit().putBoolean(FIRST_APP_START_KEY, false).apply()
        result
    }

    init {
        internalIsFirstAppStart
    }

    private var appRateDto: AppRateDto = AppRateDto(0)
    private val needShowRate = MutableSharedFlow<Boolean>()

    override fun setCurrentTheme(theme: CurrentTheme) {
        sharedPreferences.edit().putString(THEME_KEY, theme.name).apply()
    }

    override fun getCurrentTheme(): CurrentTheme {
        val fromSp = sharedPreferences.getString(THEME_KEY, null) ?: return CurrentTheme.SYSTEM
        return if (CurrentTheme.values().map { it.name }.contains(fromSp)) CurrentTheme.valueOf(fromSp)
        else CurrentTheme.SYSTEM
    }

    override fun setLastVersionCode(versionCode: Int) {
        sharedPreferences.edit().putInt(VERSION_CODE_KEY, versionCode).apply()
    }

    override fun getLastVersionCode(): Int {
        return sharedPreferences.getInt(VERSION_CODE_KEY, 0)
    }

    private var showedRateDialog = false

    override fun needShowRateRequest(): Flow<Boolean> = needShowRate

    private fun innerNeedShowRateRequest(): Boolean {
        val rateDto = appRateDto
        val result = !showedRateDialog && rateDto.needShowRateRequest(internalIsFirstAppStart)
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
        needShowRate.tryEmit(innerNeedShowRateRequest())
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

    override fun isFirstAppStart(): Boolean {
        return internalIsFirstAppStart
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