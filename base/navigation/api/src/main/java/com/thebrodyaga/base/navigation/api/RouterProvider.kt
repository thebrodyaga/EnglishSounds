package com.thebrodyaga.base.navigation.api

import com.thebrodyaga.base.navigation.api.router.AppRouter
import com.thebrodyaga.base.navigation.api.router.FeatureRouter
import com.thebrodyaga.base.navigation.api.router.TabRouter

interface RouterProvider {

    val appRouter: AppRouter
    val tabRouter: TabRouter?
    val featureRouter: FeatureRouter?

    /**
     * return first finding router: featureRouter ?: tabRouter ?: appRouter
     */
    val anyRouter: AppRouter

    fun finishFlow() {
        when (val router = anyRouter) {
            is FeatureRouter -> router.finishFeature()
            is TabRouter -> router.backToRoot()
            else -> router.finishChain()
        }
    }
}