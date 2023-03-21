package com.thebrodyaga.base.navigation.impl

import androidx.fragment.app.Fragment
import com.thebrodyaga.base.navigation.api.AppRouter
import com.thebrodyaga.base.navigation.api.RouterProvider
import com.thebrodyaga.base.navigation.api.container.FeatureContainer
import com.thebrodyaga.base.navigation.api.container.TabContainer

fun Fragment.routerProvider(appRouter: AppRouter): RouterProvider =
    RouterProviderImpl(this, appRouter)


class RouterProviderImpl constructor(
    fragment: Fragment,
    override val appRouter: AppRouter
) : RouterProvider {

    override val tabRouter: AppRouter?
    override val featureRouter: AppRouter?

    init {
        featureRouter = getFeatureRouter(fragment)
        tabRouter = getTabRouter(fragment)
    }

    override val anyRouter: AppRouter = featureRouter ?: tabRouter ?: appRouter

    private fun getFeatureRouter(fragment: Fragment): AppRouter? {
        var parentFragment: Fragment? = fragment.parentFragment
        while (parentFragment != null) {
            if (parentFragment is FeatureContainer) return parentFragment.featureRouter
            parentFragment = parentFragment.parentFragment
        }
        return null
    }

    private fun getTabRouter(fragment: Fragment): AppRouter? {
        var parentFragment: Fragment? = fragment.parentFragment
        while (parentFragment != null) {
            if (parentFragment is TabContainer) return parentFragment.tabRouter
            parentFragment = parentFragment.parentFragment
        }
        return null
    }
}