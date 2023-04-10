package com.thebrodyaga.englishsounds.base.di

import android.app.Application
import com.thebrodyaga.base.navigation.api.CiceroneHolder
import com.thebrodyaga.base.navigation.api.router.AppRouter
import com.thebrodyaga.core.utils.coroutines.AppDispatchers
import com.thebrodyaga.core.utils.coroutines.AppScope

interface AppDependencies {

    fun application(): Application
    fun appScope(): AppScope
    fun appDispatchers(): AppDispatchers

    fun appRouter(): AppRouter
    fun ciceroneHolder(): CiceroneHolder
}

interface ActivityDependencies