package com.thebrodyaga.base.navigation.impl.transition

import android.view.View
import androidx.core.view.ViewCompat
import com.thebrodyaga.base.navigation.api.TransitionInfo

val View.sharedElementBox: SharedElementBox?
    get() {
        val transitionName = ViewCompat.getTransitionName(this)
        return transitionName?.let {
            SharedElementBox(this, transitionName)
        }
    }

data class SharedElementBox(
    val sharedElement: View,
    val sharedElementName: String,
) : TransitionInfo