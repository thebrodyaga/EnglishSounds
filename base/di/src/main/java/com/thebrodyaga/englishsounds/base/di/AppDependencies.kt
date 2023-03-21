package com.thebrodyaga.englishsounds.base.di

import androidx.fragment.app.Fragment
import android.app.Activity
import com.thebrodyaga.base.navigation.api.AppRouter
import com.thebrodyaga.core.navigation.api.cicerone.NavigatorHolder
import com.thebrodyaga.core.navigation.api.cicerone.Router

@Deprecated("")
fun Fragment.findComponent(): AppDependencies =
    (requireContext().applicationContext as ComponentHolder).appComponent

fun Activity.findComponent(): AppDependencies =
    (applicationContext as ComponentHolder).appComponent

@Deprecated("")
interface ComponentHolder {
    val appComponent: AppDependencies
}

interface AppDependencies {

    fun getRouter(): Router
    fun appRouter(): AppRouter
    fun getNavigatorHolder(): NavigatorHolder
}