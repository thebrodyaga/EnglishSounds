package com.thebrodyaga.englishsounds.base.di

import com.thebrodyaga.base.navigation.api.router.AppRouter
import com.thebrodyaga.core.navigation.api.cicerone.NavigatorHolder
import com.thebrodyaga.core.navigation.api.cicerone.Router

interface AppDependencies {

    fun getRouter(): Router
    fun appRouter(): AppRouter
    fun getNavigatorHolder(): NavigatorHolder
}