package com.thebrodyaga.base.navigation.api

interface RouterProvider {

    val appRouter: AppRouter

    /**
     * not null if fragment in the TabContainer
     */
    val tabRouter: AppRouter?

    /**
     * not null if fragment in the FlowContainer
     */
    val flowRouter: AppRouter?

    /**
     * return first finding router - flowRouter ?: tabRouter ?: appRouter
     */
    val anyRouter: AppRouter
}