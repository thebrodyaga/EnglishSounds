package com.thebrodyaga.feature.youtube.api

import java.io.Serializable

data class PlayVideoExtra constructor(
    val videoId: String,
    val videoName: String
) : Serializable

interface YoutubeScreenFactory {
}