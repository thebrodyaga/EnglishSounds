package com.thebrodyaga.englishsounds.base.di

import androidx.fragment.app.Fragment
import android.app.Activity
import com.thebrodyaga.core.navigation.api.cicerone.NavigatorHolder
import com.thebrodyaga.core.navigation.api.cicerone.Router

fun Fragment.findComponent(): AppDependencies =
    (requireContext().applicationContext as ComponentHolder).appComponent

fun Activity.findComponent(): AppDependencies =
    (applicationContext as ComponentHolder).appComponent

interface ComponentHolder {
    val appComponent: AppDependencies
}

interface AppDependencies {

    fun getRouter(): Router
    fun getNavigatorHolder(): NavigatorHolder
}