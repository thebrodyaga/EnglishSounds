package com.thebrodyaga.feature.mainScreen.api

import com.thebrodyaga.base.navigation.api.AppFragmentScreen

interface MainScreenFactory {

    fun mainScreen(): AppFragmentScreen
}