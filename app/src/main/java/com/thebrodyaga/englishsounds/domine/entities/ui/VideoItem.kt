package com.thebrodyaga.englishsounds.domine.entities.ui

import com.thebrodyaga.englishsounds.domine.entities.data.AmericanSoundDto

sealed class VideoItem constructor(
    open val videoId: String,
    open val title: String
)

data class ContrastingSoundVideoItem constructor(
    override val videoId: String,
    override val title: String,
    val firstTranscription: AmericanSoundDto?,
    val secondTranscription: AmericanSoundDto?
) : VideoItem(videoId, title)

data class MostCommonWordsVideoItem constructor(
    override val videoId: String,
    override val title: String
) : VideoItem(videoId, title)

data class AdvancedExercisesVideoVideoItem constructor(
    override val videoId: String,
    override val title: String
) : VideoItem(videoId, title)