package com.thebrodyaga.englishsounds.base.navigation.api

interface RouterProvider {

    val appRouter: AppRouter
    val tabRouter: AppRouter?

    val anyRoute: AppRouter
}