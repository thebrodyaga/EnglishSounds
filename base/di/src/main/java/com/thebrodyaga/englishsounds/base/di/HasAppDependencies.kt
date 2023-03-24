package com.thebrodyaga.englishsounds.base.di

import android.app.Activity
import android.app.Service
import androidx.fragment.app.Fragment

interface HasAppDependencies {
    val dependencies: AppDependencies
}

interface HasActivityDependencies {
    val dependencies: ActivityDependencies
}

inline fun <reified T> Fragment.findActivityDependencies(): T {
    val activity = requireActivity()
    if (activity !is HasActivityDependencies) throw IllegalStateException("Can not find dependencies provider for $this")
    if (activity.dependencies !is T) throw IllegalStateException("Can not find dependencies provider for $this")

    return activity.dependencies as T
}

inline fun <reified T> Fragment.findDependencies(): T {
    val application = requireActivity().application
    if (application !is HasAppDependencies) throw IllegalStateException("Can not find dependencies provider for $this")
    if (application.dependencies !is T) throw IllegalStateException("Can not find dependencies provider for $this")

    return application.dependencies as T
}

inline fun <reified T> Activity.findDependencies(): T {
    val app = application ?: throw IllegalStateException("Application is null")
    if (app !is HasAppDependencies) throw IllegalStateException("Can not find dependencies provider for $this")
    if (app.dependencies !is T) throw IllegalStateException("Can not find dependencies provider for $this")

    return app.dependencies as T
}

inline fun <reified T> Service.findDependencies(): T {
    val application = application
    if (application !is HasAppDependencies) throw IllegalStateException("Can not find dependencies provider for $this")
    if (application.dependencies !is T) throw IllegalStateException("Can not find dependencies provider for $this")

    return application.dependencies as T
}