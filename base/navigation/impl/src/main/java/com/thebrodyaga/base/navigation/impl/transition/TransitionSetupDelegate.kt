package com.thebrodyaga.base.navigation.impl.transition

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.transition.Transition
import com.google.android.material.color.MaterialColors
import com.google.android.material.transition.MaterialContainerTransform
import com.thebrodyaga.base.navigation.impl.R
import com.thebrodyaga.base.navigation.impl.navigator.AppNavigator
import javax.inject.Inject

class TransitionSetupDelegate @Inject constructor(
) {

    fun setupTransition(
        containerId: Int,
        currentFragment: Fragment,
        nextFragment: Fragment,
        fragmentTransaction: FragmentTransaction,
        sharedElementBox: SharedElementBox?,
    ) {

        if (sharedElementBox != null)
            setupForwardSharedTransition(
                containerId,
                currentFragment,
                nextFragment,
                fragmentTransaction,
                sharedElementBox
            )
        else setupForwardTransaction(currentFragment, nextFragment)

        setupBackTransaction(currentFragment, nextFragment)

    }

    private fun setupForwardSharedTransition(
        containerId: Int,
        currentFragment: Fragment,
        nextFragment: Fragment,
        fragmentTransaction: FragmentTransaction,
        sharedElementBox: SharedElementBox,
    ) {
        val currentFragmentView = currentFragment.view ?: return

        val sharedElement = sharedElementBox.sharedElement
        val sharedElementName = sharedElementBox.sharedElementName
        fragmentTransaction.addSharedElement(sharedElement, sharedElementName)

        val context = currentFragmentView.context

        val nextEnterTransition = MaterialContainerTransform(context, true).apply {
            val colorSurface = MaterialColors.getColor(sharedElement, R.attr.colorSurface)
            containerColor = colorSurface
            drawingViewId = containerId
            startView = sharedElement

            fadeMode = MaterialContainerTransform.FADE_MODE_THROUGH
            fadeProgressThresholds = MaterialContainerTransform.ProgressThresholds(0f, 1f)
        }

        val exitTransition = TransitionProvider.scale(false)
        exitTransition.addTarget(currentFragmentView.id)
        exitTransition.duration = nextEnterTransition.duration

        currentFragment.exitTransition = exitTransition
        nextFragment.sharedElementEnterTransition = nextEnterTransition
        nextFragment.sharedElementReturnTransition = null

        val arguments = nextFragment.arguments
        if (arguments == null) {
            val args = Bundle()
            args.putString(AppNavigator.ARG_TRANSITION_NAME, sharedElementName)
            nextFragment.arguments = args
        } else {
            arguments.putString(AppNavigator.ARG_TRANSITION_NAME, sharedElementName)
        }
    }


    private fun setupForwardTransaction(
        currentFragment: Fragment,
        nextFragment: Fragment,
    ) {
        val currentFragmentView = currentFragment.view ?: return

        val backTransition = getForwardTransition()
        val exitTransition = backTransition.firstFragment ?: return
        exitTransition.addTarget(currentFragmentView.id)
        val enterTransition = backTransition.secondFragment ?: return

        currentFragment.exitTransition = exitTransition
        nextFragment.enterTransition = enterTransition
    }


    private fun setupBackTransaction(
        currentFragment: Fragment,
        nextFragment: Fragment,
    ) {
        val currentFragmentView = currentFragment.view ?: return

        val backTransition = getBackTransition()
        val reenterTransition = backTransition.firstFragment ?: return
        reenterTransition.addTarget(currentFragmentView.id)
        val returnTransition = backTransition.secondFragment ?: return

        currentFragment.reenterTransition = reenterTransition
        nextFragment.returnTransition = returnTransition
    }

    private fun getForwardTransition(): TransitionBox {
        return TransitionBox(TransitionProvider.axis(true), TransitionProvider.axis(true))
//        return TransitionBox(TransitionProvider.scale(false), TransitionProvider.scale(true))
    }

    private fun getBackTransition(): TransitionBox {
        return TransitionBox(TransitionProvider.axis(false), TransitionProvider.axis(false))
//        return TransitionBox(TransitionProvider.scale(true), TransitionProvider.scale(false))
    }

    private data class TransitionBox(
        val firstFragment: Transition?,
        val secondFragment: Transition?,
    )
}