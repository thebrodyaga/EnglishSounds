package com.thebrodyaga.feature.soundDetails.api

import com.thebrodyaga.core.navigation.api.cicerone.Screen

interface SoundDetailsScreenFactory {

    fun soundDetailsScreen(transcription: String): Screen
}