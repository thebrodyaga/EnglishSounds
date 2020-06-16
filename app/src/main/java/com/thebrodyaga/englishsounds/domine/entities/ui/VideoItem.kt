package com.thebrodyaga.englishsounds.domine.entities.ui

import com.thebrodyaga.englishsounds.domine.entities.data.AmericanSoundDto
import com.thebrodyaga.englishsounds.domine.entities.resources.SoundVideoRes

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

data class AdvancedExercisesVideoItem constructor(
    override val videoId: String,
    override val title: String,
    val firstTranscription: AmericanSoundDto?,
    val secondTranscription: AmericanSoundDto?
) : VideoItem(videoId, title)

data class SoundVideoItem constructor(
    override val videoId: String,
    override val title: String,
    val sound: AmericanSoundDto?
) : VideoItem(videoId, title) {
    constructor(soundVideoRes: SoundVideoRes, soundDto: AmericanSoundDto?, title: String)
            : this(soundVideoRes.videoId, title, soundDto)
}