package com.thebrodyaga.feature.youtube.api

import com.thebrodyaga.core.navigation.api.cicerone.Screen
import java.io.Serializable

data class PlayVideoExtra constructor(
    val videoId: String,
    val videoName: String
) : Serializable

interface YoutubeScreenFactory {

    fun youtubeScreen(playVideoExtra: PlayVideoExtra): Screen
}