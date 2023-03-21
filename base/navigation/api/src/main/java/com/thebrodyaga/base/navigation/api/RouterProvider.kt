package com.thebrodyaga.base.navigation.api

interface RouterProvider {

    val appRouter: AppRouter
    val tabRouter: AppRouter?
    val featureRouter: AppRouter?

    /**
     * return first finding router: featureRouter ?: tabRouter ?: appRouter
     */
    val anyRouter: AppRouter
}