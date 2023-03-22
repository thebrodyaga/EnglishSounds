package com.thebrodyaga.feature.soundDetails.api

import com.thebrodyaga.base.navigation.api.FragmentTransactionBox
import com.thebrodyaga.core.navigation.api.cicerone.Screen

interface SoundDetailsScreenFactory {

    fun soundDetailsScreen(transcription: String, sharedElement: FragmentTransactionBox? = null): Screen
}