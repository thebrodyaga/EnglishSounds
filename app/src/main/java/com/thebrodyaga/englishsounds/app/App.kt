package com.thebrodyaga.englishsounds.app

import androidx.appcompat.app.AppCompatDelegate
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.thebrodyaga.englishsounds.BuildConfig
import com.thebrodyaga.englishsounds.analytics.AnalyticsEngine
import com.thebrodyaga.englishsounds.base.app.BaseApp
import com.thebrodyaga.englishsounds.di.AppComponent
import com.thebrodyaga.englishsounds.di.DaggerAppComponent
import com.thebrodyaga.feature.setting.api.CurrentTheme
import com.thebrodyaga.feature.setting.api.SettingManager
import dev.shreyaspatil.permissionFlow.PermissionFlow
import timber.log.Timber
import timber.log.Timber.DebugTree
import javax.inject.Inject

class App : BaseApp() {

    @Inject
    lateinit var settingManager: SettingManager

    override val appComponent: AppComponent = DaggerAppComponent.builder()
        .application(this)
        .build()
        .also { component = it }

    override fun onCreate() {
        app = this
        appComponent.inject(this)
        super.onCreate()
        PermissionFlow.init(this)
        AnalyticsEngine.firebaseAnalytics = FirebaseAnalytics.getInstance(this)
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
        settingManager.updateTheme()
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
        @Deprecated("delete")
        val appComponent: AppComponent
            get() = app.appComponent
        lateinit var app: App
    }
}