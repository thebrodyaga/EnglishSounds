package com.thebrodyaga.englishsounds.base.navigation.impl

import com.thebrodyaga.englishsounds.base.navigation.api.AppRouter
import com.thebrodyaga.englishsounds.base.navigation.api.CiceroneHolder
import com.thebrodyaga.englishsounds.base.navigation.api.RouterProvider
import com.thebrodyaga.englishsounds.base.navigation.api.cicerone.Cicerone
import javax.inject.Inject

class RouterProviderImpl @Inject constructor(
    private val appCicerone: Cicerone<AppRouter>
) : RouterProvider, CiceroneHolder {

    private val containers = mutableMapOf<String, Cicerone<AppRouter>>()

    override val appRouter: AppRouter = appCicerone.router
    override val tabRouter: AppRouter?
        get() = TODO() /*containers[""]?.router*/
    override val anyRoute: AppRouter
        get() = tabRouter ?: appRouter

    override fun getCicerone(containerTag: String, defaultValue: Cicerone<AppRouter>): Cicerone<AppRouter> {
        return containers.getOrPut(containerTag) { defaultValue }
    }

    override fun clearCicerone(containerTag: String) {
        containers.remove(containerTag)
    }
}