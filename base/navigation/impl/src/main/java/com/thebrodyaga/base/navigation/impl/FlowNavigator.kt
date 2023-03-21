package com.thebrodyaga.base.navigation.impl

import androidx.fragment.app.Fragment
import com.thebrodyaga.core.navigation.impl.cicerone.AppNavigator

class FlowNavigator constructor(
    private val fragment: Fragment,
    private val flowContainerId: Int,
) : AppNavigator(
    activity = fragment.requireActivity(),
    containerId = flowContainerId,
    fragmentManager = fragment.childFragmentManager,
) {

    override fun activityBack() {
        fragment.parentFragmentManager.popBackStack()
    }
}