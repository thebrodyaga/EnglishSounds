package com.thebrodyaga.core.navigation.impl.cicerone

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.thebrodyaga.core.navigation.api.cicerone.Screen

fun interface Creator<A, R> {
    fun create(argument: A): R
}

interface FragmentScreen : Screen {
    val clearContainer: Boolean get() = true
    fun createFragment(factory: FragmentFactory): Fragment

    companion object {
        operator fun invoke(
            key: String? = null,
            clearContainer: Boolean = true,
            fragmentCreator: Creator<FragmentFactory, Fragment>
        ) = object : FragmentScreen {
            override val screenKey = key ?: fragmentCreator::class.java.name
            override val clearContainer = clearContainer
            override fun createFragment(factory: FragmentFactory) = fragmentCreator.create(factory)
        }
    }
}

interface ActivityScreen : Screen {
    val startActivityOptions: Bundle? get() = null
    fun createIntent(context: Context): Intent

    companion object {
        operator fun invoke(
            key: String? = null,
            startActivityOptions: Bundle? = null,
            intentCreator: Creator<Context, Intent>
        ) = object : ActivityScreen {
            override val screenKey = key ?: intentCreator::class.java.name
            override val startActivityOptions = startActivityOptions
            override fun createIntent(context: Context) = intentCreator.create(context)
        }
    }
}