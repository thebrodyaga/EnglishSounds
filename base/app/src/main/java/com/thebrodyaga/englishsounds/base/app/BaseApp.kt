package com.thebrodyaga.englishsounds.base.app

import android.app.Application
import com.thebrodyaga.englishsounds.base.di.AppDependencies
import com.thebrodyaga.englishsounds.base.di.ComponentHolder
import com.thebrodyaga.englishsounds.base.di.HasComponentDependencies

abstract class BaseApp : Application(), ComponentHolder, HasComponentDependencies {

    companion object {
        lateinit var component: AppDependencies
    }

    override fun onCreate() {
        super.onCreate()
    }

    override val dependencies: AppDependencies
        get() = component
}