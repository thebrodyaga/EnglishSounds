package com.thebrodyaga.feature.soundList.api

import com.thebrodyaga.core.navigation.api.cicerone.Screen

interface SoundListScreenFactory {

    fun soundListFactory(): Screen
}