package com.thebrodyaga.base.navigation.impl

import androidx.fragment.app.Fragment
import com.thebrodyaga.base.navigation.api.AppRouter
import com.thebrodyaga.base.navigation.api.RouterProvider
import com.thebrodyaga.base.navigation.api.container.FlowContainer
import com.thebrodyaga.base.navigation.api.container.TabContainer

fun Fragment.routerProvider(appRouter: AppRouter): RouterProvider =
    RouterProviderImpl(this, appRouter)


class RouterProviderImpl constructor(
    fragment: Fragment,
    override val appRouter: AppRouter
) : RouterProvider {

    override val tabRouter: AppRouter?
    override val flowRouter: AppRouter?

    init {
        flowRouter = getFlowRouter(fragment)
        tabRouter = getTabRouter(fragment)
    }

    override val anyRouter: AppRouter = flowRouter ?: tabRouter ?: appRouter

    private fun getFlowRouter(fragment: Fragment): AppRouter? {
        var parentFragment: Fragment? = fragment.parentFragment
        while (parentFragment != null) {
            if (parentFragment is FlowContainer) return parentFragment.localRouter
            parentFragment = parentFragment.parentFragment
        }
        return null
    }

    private fun getTabRouter(fragment: Fragment): AppRouter? {
        var parentFragment: Fragment? = fragment.parentFragment
        while (parentFragment != null) {
            if (parentFragment is TabContainer) return parentFragment.localRouter
            parentFragment = parentFragment.parentFragment
        }
        return null
    }
}