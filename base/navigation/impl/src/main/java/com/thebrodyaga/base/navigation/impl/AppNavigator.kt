package com.thebrodyaga.base.navigation.impl

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.color.MaterialColors
import com.google.android.material.transition.Hold
import com.google.android.material.transition.MaterialContainerTransform
import com.thebrodyaga.base.navigation.api.AppFragmentScreen
import com.thebrodyaga.base.navigation.api.FragmentTransactionBox
import com.thebrodyaga.core.navigation.impl.cicerone.CiceroneNavigator
import com.thebrodyaga.core.navigation.impl.cicerone.FragmentScreen

open class AppNavigator(
    activity: FragmentActivity,
    containerId: Int,
    fragmentManager: FragmentManager = activity.supportFragmentManager,
    fragmentFactory: FragmentFactory = fragmentManager.fragmentFactory
) : CiceroneNavigator(activity, containerId, fragmentManager, fragmentFactory) {

    companion object {
        const val ARG_TRANSITION_NAME = "ARG_TRANSITION_NAME"
    }

    override fun setupFragmentTransaction(
        screen: FragmentScreen,
        fragmentTransaction: FragmentTransaction,
        currentFragment: Fragment?,
        nextFragment: Fragment
    ) {
        currentFragment ?: return

        if (screen is AppFragmentScreen) {
            screen.sharedElement?.let { sharedElement ->
                setupTransformTransaction(currentFragment, nextFragment, fragmentTransaction, sharedElement)
            }
        }
    }

    private fun setupTransformTransaction(
        currentFragment: Fragment,
        nextFragment: Fragment,
        fragmentTransaction: FragmentTransaction,
        transactionBox: FragmentTransactionBox
    ) {
        val currentFragmentView = currentFragment.view ?: return

        val sharedElement = transactionBox.sharedElement
        val sharedElementName = transactionBox.sharedElementName

        val context = currentFragment.requireContext()
        val transform = MaterialContainerTransform(context,  /* entering= */true).apply {
            val colorSurface = MaterialColors.getColor(sharedElement, R.attr.colorSurface)
            val colorBackground = MaterialColors.getColor(sharedElement, android.R.attr.colorBackground)
            val transparent = Color.TRANSPARENT
            containerColor = colorSurface
            drawingViewId = containerId
            startView = sharedElement
            fadeMode = MaterialContainerTransform.FADE_MODE_THROUGH
        }

        nextFragment.sharedElementEnterTransition = transform

        fragmentTransaction.addSharedElement(sharedElement, sharedElementName)

        val hold = Hold()
        // Add root view as target for the Hold so that the entire view hierarchy is held in place as
        // one instead of each child view individually. Helps keep shadows during the transition.
        // Add root view as target for the Hold so that the entire view hierarchy is held in place as
        // one instead of each child view individually. Helps keep shadows during the transition.
        hold.addTarget(currentFragmentView)
        hold.duration = transform.duration
        currentFragment.exitTransition = hold

        val arguments = nextFragment.arguments
        if (arguments == null) {
            val args = Bundle()
            args.putString(ARG_TRANSITION_NAME, sharedElementName)
            nextFragment.arguments = args
        } else {
            arguments.putString(ARG_TRANSITION_NAME, sharedElementName)
        }
    }
}