package com.thebrodyaga.englishsounds.base.app

import android.app.Application
import com.thebrodyaga.englishsounds.base.di.AppDependencies
import com.thebrodyaga.englishsounds.base.di.HasAppDependencies

abstract class BaseApp : Application(), HasAppDependencies {

    companion object {
        lateinit var component: AppDependencies
    }

    override fun onCreate() {
        super.onCreate()
    }

    override val dependencies: AppDependencies
        get() = component
}