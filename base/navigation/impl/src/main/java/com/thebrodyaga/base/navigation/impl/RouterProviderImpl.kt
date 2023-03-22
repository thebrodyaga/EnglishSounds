package com.thebrodyaga.base.navigation.impl

import androidx.fragment.app.Fragment
import com.thebrodyaga.base.navigation.api.router.AppRouter
import com.thebrodyaga.base.navigation.api.RouterProvider
import com.thebrodyaga.base.navigation.api.container.FeatureContainer
import com.thebrodyaga.base.navigation.api.container.TabContainer
import com.thebrodyaga.base.navigation.api.router.FeatureRouter
import com.thebrodyaga.base.navigation.api.router.TabRouter
import com.thebrodyaga.englishsounds.base.di.AppDependencies
import com.thebrodyaga.englishsounds.base.di.findDependencies

fun Fragment.createRouterProvider(): RouterProvider {
    val appRouter: AppRouter = findDependencies<AppDependencies>().appRouter()
    return RouterProviderImpl(this, appRouter)
}


class RouterProviderImpl constructor(
    fragment: Fragment,
    override val appRouter: AppRouter
) : RouterProvider {

    override val tabRouter: TabRouter?
    override val featureRouter: FeatureRouter?

    init {
        featureRouter = getFeatureRouter(fragment)
        tabRouter = getTabRouter(fragment)
    }

    override val anyRouter: AppRouter = featureRouter ?: tabRouter ?: appRouter

    private fun getFeatureRouter(fragment: Fragment): FeatureRouter? {
        var parentFragment: Fragment? = fragment.parentFragment
        while (parentFragment != null) {
            if (parentFragment is FeatureContainer) return parentFragment.featureRouter
            parentFragment = parentFragment.parentFragment
        }
        return null
    }

    private fun getTabRouter(fragment: Fragment): TabRouter? {
        var parentFragment: Fragment? = fragment.parentFragment
        while (parentFragment != null) {
            if (parentFragment is TabContainer) return parentFragment.tabRouter
            parentFragment = parentFragment.parentFragment
        }
        return null
    }
}