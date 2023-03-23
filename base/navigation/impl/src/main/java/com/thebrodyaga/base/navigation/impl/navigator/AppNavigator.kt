package com.thebrodyaga.base.navigation.impl.navigator

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.thebrodyaga.base.navigation.impl.AppFragmentScreen
import com.thebrodyaga.base.navigation.impl.transition.SharedElementBox
import com.thebrodyaga.base.navigation.impl.transition.TransitionSetupDelegate
import com.thebrodyaga.core.navigation.impl.cicerone.CiceroneNavigator
import com.thebrodyaga.core.navigation.impl.cicerone.FragmentScreen
import com.thebrodyaga.englishsounds.base.di.AppDependencies
import com.thebrodyaga.englishsounds.base.di.findDependencies

open class AppNavigator(
    activity: FragmentActivity,
    containerId: Int,
    fragmentManager: FragmentManager = activity.supportFragmentManager,
    fragmentFactory: FragmentFactory = fragmentManager.fragmentFactory
) : CiceroneNavigator(activity, containerId, fragmentManager, fragmentFactory) {

    companion object {
        const val ARG_TRANSITION_NAME = "ARG_TRANSITION_NAME"
    }
    private var transitionSetupDelegate: TransitionSetupDelegate = TransitionSetupDelegate()

    override fun setupFragmentTransaction(
        screen: FragmentScreen,
        fragmentTransaction: FragmentTransaction,
        currentFragment: Fragment?,
        nextFragment: Fragment
    ) {
        currentFragment ?: return

        val sharedElement = ((screen as? AppFragmentScreen)?.transitionInfo as? SharedElementBox)
        transitionSetupDelegate.setupTransition(
            containerId, currentFragment, nextFragment, fragmentTransaction, sharedElement
        )
    }
}