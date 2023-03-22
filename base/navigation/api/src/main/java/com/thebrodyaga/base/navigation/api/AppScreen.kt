package com.thebrodyaga.base.navigation.api

import android.view.View
import androidx.core.view.ViewCompat
import com.thebrodyaga.core.navigation.impl.cicerone.FragmentScreen

val View.fragmentTransactionBox: FragmentTransactionBox?
    get() {
        val transitionName = ViewCompat.getTransitionName(this)
        return transitionName?.let {
            FragmentTransactionBox(this, transitionName)
        }
    }

data class FragmentTransactionBox(
    val sharedElement: View,
    val sharedElementName: String,
)

interface AppFragmentScreen : FragmentScreen {
    val sharedElement: FragmentTransactionBox?
        get() = null
}