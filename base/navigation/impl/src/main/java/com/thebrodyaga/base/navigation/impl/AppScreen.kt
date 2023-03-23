package com.thebrodyaga.base.navigation.impl

import com.thebrodyaga.base.navigation.api.TransitionInfo
import com.thebrodyaga.core.navigation.impl.cicerone.FragmentScreen

interface AppFragmentScreen : FragmentScreen {
    val transitionInfo: TransitionInfo?
        get() = null
}