package com.thebrodyaga.base.navigation.impl

import androidx.fragment.app.Fragment
import com.thebrodyaga.base.navigation.api.RouterProvider
import com.thebrodyaga.base.navigation.api.container.TabsContainer
import com.thebrodyaga.core.navigation.impl.cicerone.CiceroneNavigator

class FlowNavigator constructor(
    private val fragment: Fragment,
    private val flowContainerId: Int,
    private val routerProvider: RouterProvider,
) : CiceroneNavigator(
    activity = fragment.requireActivity(),
    containerId = flowContainerId,
    fragmentManager = fragment.childFragmentManager,
) {

    override fun activityBack() {
        val parentFragment = fragment.parentFragment
        if (parentFragment is TabsContainer)
            parentFragment.onBackPressed()
        else routerProvider.anyRouter.exit()
    }
}