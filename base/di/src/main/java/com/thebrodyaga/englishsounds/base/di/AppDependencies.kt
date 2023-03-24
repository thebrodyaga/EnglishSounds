package com.thebrodyaga.englishsounds.base.di

import com.thebrodyaga.base.navigation.api.router.AppRouter
import com.thebrodyaga.core.navigation.api.cicerone.NavigatorHolder
import com.thebrodyaga.core.navigation.api.cicerone.CiceroneRouter
import com.thebrodyaga.core.utils.coroutines.AppDispatchers
import com.thebrodyaga.core.utils.coroutines.AppScope

interface AppDependencies {

    fun appScope(): AppScope
    fun appDispatchers(): AppDispatchers

    fun getRouter(): CiceroneRouter
    fun appRouter(): AppRouter
    fun getNavigatorHolder(): NavigatorHolder
}