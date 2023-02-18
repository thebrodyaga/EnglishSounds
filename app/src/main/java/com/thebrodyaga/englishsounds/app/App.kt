package com.thebrodyaga.englishsounds.app

import androidx.appcompat.app.AppCompatDelegate
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.thebrodyaga.englishsounds.BuildConfig
import com.thebrodyaga.englishsounds.base.app.BaseApp
import com.thebrodyaga.englishsounds.di.AppComponent
import com.thebrodyaga.englishsounds.di.DaggerAppComponent
import com.thebrodyaga.englishsounds.tools.SettingManager
import timber.log.Timber
import timber.log.Timber.DebugTree

class App : BaseApp() {

    override fun onCreate() {
        app = this
        component = DaggerAppComponent.builder()
            .application(this)
            .build()
        super.onCreate()
//        MobileAds.initialize(this)
        if (!BuildConfig.DEBUG) {
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
            Timber.plant(CrashReportingTree())
        } else {
            Timber.plant(DebugTree())
        }
        updateTheme()
    }

    fun updateTheme() {
        AppCompatDelegate.setDefaultNightMode(
            when (appComponent.getSettingManager().getCurrentTheme()) {
                SettingManager.CurrentTheme.SYSTEM -> {
                    AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                }
                SettingManager.CurrentTheme.DARK -> {
                    AppCompatDelegate.MODE_NIGHT_YES
                }
                SettingManager.CurrentTheme.LIGHT -> {
                    AppCompatDelegate.MODE_NIGHT_NO
                }
            }
        )
    }

    private inner class CrashReportingTree : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, throwable: Throwable?) {
            val logTag = when (priority) {
                Log.VERBOSE -> "V"
                Log.DEBUG -> "D"
                Log.INFO -> "I"
                Log.WARN -> "W"
                Log.ERROR -> "E"
                Log.ASSERT -> "A"
                else -> "NON"
            }
            FirebaseCrashlytics.getInstance().log("$logTag/$tag: $message")
            if (throwable != null) {
                FirebaseCrashlytics.getInstance().log("$logTag/$tag: ${throwable.message ?: ""}")
            }
        }
    }

    companion object {
        val appComponent: AppComponent
            get() = component as AppComponent
        lateinit var app: App
    }
}