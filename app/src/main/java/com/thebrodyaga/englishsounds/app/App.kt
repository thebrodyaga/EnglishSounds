package com.thebrodyaga.englishsounds.app

import android.app.Application
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.crashlytics.android.Crashlytics
import com.thebrodyaga.englishsounds.BuildConfig
import com.thebrodyaga.englishsounds.di.AppComponent
import com.thebrodyaga.englishsounds.di.DaggerAppComponent
import com.thebrodyaga.englishsounds.tools.SettingManager
import io.fabric.sdk.android.Fabric
import timber.log.Timber
import timber.log.Timber.DebugTree

class App : Application() {

    override fun onCreate() {
        app = this
        appComponent = DaggerAppComponent.builder().application(this).build()
        super.onCreate()
        if (!BuildConfig.DEBUG) {
            Fabric.with(this, Crashlytics())
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
            Crashlytics.log(priority, tag, message)
            if (throwable != null) {
                Crashlytics.logException(throwable)
            }
        }
    }

    companion object {
        lateinit var appComponent: AppComponent
        lateinit var app: App
    }
}