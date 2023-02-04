package com.thebrodyaga.englishsounds.base.app

import android.app.Application
import com.thebrodyaga.englishsounds.base.di.BaseAppComponent
import com.thebrodyaga.englishsounds.base.di.ComponentHolder

abstract class BaseApp : Application(), ComponentHolder {

    companion object {
        lateinit var component: BaseAppComponent
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun component(): BaseAppComponent = component
}