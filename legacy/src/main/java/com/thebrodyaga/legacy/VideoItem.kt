package com.thebrodyaga.legacy

import com.thebrodyaga.data.sounds.api.model.AmericanSoundDto
import com.thebrodyaga.data.sounds.api.model.SoundVideoRes

interface VideoItemInList

sealed class VideoItem constructor(
    open val videoId: String,
    open val title: String
) : VideoItemInList

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