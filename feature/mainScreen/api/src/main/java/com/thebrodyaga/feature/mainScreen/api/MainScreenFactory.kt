package com.thebrodyaga.feature.mainScreen.api

import com.thebrodyaga.core.navigation.api.cicerone.Screen

interface MainScreenFactory {

    fun mainScreen(): Screen
}