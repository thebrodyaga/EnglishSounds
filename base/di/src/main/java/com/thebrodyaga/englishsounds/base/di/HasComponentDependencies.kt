package com.thebrodyaga.englishsounds.base.di

import androidx.fragment.app.Fragment
import android.app.Activity
import android.app.Service

interface HasComponentDependencies {
    val dependencies: AppDependencies
}

inline fun <reified T> Fragment.findDependencies(): T {
    val application = requireActivity().application
    if (application !is HasComponentDependencies) throw IllegalStateException("Can not find dependencies provider for $this")
    if (application.dependencies !is T) throw IllegalStateException("Can not find dependencies provider for $this")

    return application.dependencies as T
}

inline fun <reified T> Activity.findDependencies(): T {
    val app = application ?: throw IllegalStateException("Application is null")
    if (app !is HasComponentDependencies) throw IllegalStateException("Can not find dependencies provider for $this")
    if (app.dependencies !is T) throw IllegalStateException("Can not find dependencies provider for $this")

    return app.dependencies as T
}

inline fun <reified T> Service.findDependencies(): T {
    val application = application
    if (application !is HasComponentDependencies) throw IllegalStateException("Can not find dependencies provider for $this")
    if (application.dependencies !is T) throw IllegalStateException("Can not find dependencies provider for $this")

    return application.dependencies as T
}