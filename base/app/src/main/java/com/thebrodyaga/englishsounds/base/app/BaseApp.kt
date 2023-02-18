package com.thebrodyaga.englishsounds.base.app

import android.app.Application
import com.google.firebase.analytics.FirebaseAnalytics
import com.thebrodyaga.englishsounds.analytics.AnalyticsEngine
import com.thebrodyaga.englishsounds.base.di.BaseAppComponent
import com.thebrodyaga.englishsounds.base.di.ComponentHolder

abstract class BaseApp : Application(), ComponentHolder {

    companion object {
        lateinit var component: BaseAppComponent
    }

    override fun onCreate() {
        super.onCreate()
        AnalyticsEngine.firebaseAnalytics = FirebaseAnalytics.getInstance(this)
    }

    override fun component(): BaseAppComponent = component
}