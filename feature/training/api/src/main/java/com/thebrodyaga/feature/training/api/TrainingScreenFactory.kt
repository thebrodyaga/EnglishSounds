package com.thebrodyaga.feature.training.api

import com.thebrodyaga.core.navigation.api.cicerone.Screen

interface TrainingScreenFactory {

    fun trainingScreen(): Screen
}