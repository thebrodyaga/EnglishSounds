package com.thebrodyaga.core.navigation.impl

import com.thebrodyaga.core.navigation.api.AppRouter
import com.thebrodyaga.core.navigation.api.CiceroneHolder
import com.thebrodyaga.core.navigation.api.RouterProvider
import com.thebrodyaga.core.navigation.api.cicerone.Cicerone
import javax.inject.Inject

class RouterProviderImpl @Inject constructor(
    private val appCicerone: Cicerone<AppRouter>
) : RouterProvider, CiceroneHolder {

    private val containers = mutableMapOf<String, Cicerone<AppRouter>>()

    override val appRouter: AppRouter = appCicerone.router

    override fun getCicerone(containerTag: String, defaultValue: Cicerone<AppRouter>): Cicerone<AppRouter> {
        return containers.getOrPut(containerTag) { defaultValue }
    }

    override fun clearCicerone(containerTag: String) {
        containers.remove(containerTag)
    }
}