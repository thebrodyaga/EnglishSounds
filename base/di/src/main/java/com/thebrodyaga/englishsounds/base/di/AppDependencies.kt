package com.thebrodyaga.englishsounds.base.di

import com.thebrodyaga.base.navigation.api.router.AppRouter
import com.thebrodyaga.core.navigation.api.cicerone.NavigatorHolder
import com.thebrodyaga.core.navigation.api.cicerone.Router
import com.thebrodyaga.core.utils.coroutines.AppScope

interface AppDependencies {

    fun appScope(): AppScope
    fun getRouter(): Router
    fun appRouter(): AppRouter
    fun getNavigatorHolder(): NavigatorHolder
}