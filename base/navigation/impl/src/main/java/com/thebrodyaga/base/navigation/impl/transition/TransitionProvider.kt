package com.thebrodyaga.base.navigation.impl.transition

import com.google.android.material.transition.Hold
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialSharedAxis

object TransitionProvider {

    fun scale(bringToFront: Boolean): MaterialElevationScale = MaterialElevationScale(/* growing = */ bringToFront)
    fun hold(): Hold = Hold()

    /**
     * Fragment A exitTransition = (/* forward= */ true)
     * Fragment B enterTransition = (/* forward= */ true)
     *
     * Fragment A reenterTransition = (/* forward= */ false)
     * Fragment B returnTransition = (/* forward= */ false)
     */
    fun axis(
        forward: Boolean,
        @MaterialSharedAxis.Axis axis: Int = MaterialSharedAxis.Z
    ): MaterialSharedAxis = MaterialSharedAxis(/* axis = */ axis, /* forward = */ forward)
}