package com.thebrodyaga.englishsounds.domine.entities.resources

import com.thebrodyaga.englishsounds.domine.entities.data.SoundType

data class SoundVideoRes constructor(
    val transcription: String,
    val videoId: String,
    val soundType: SoundType
) : VideoFromRes()

data class ContrastingSoundVideoRes constructor(
    val firstTranscription: String,
    var secondTranscription: String,
    val videoId: String,
    val videoName: String
) : VideoFromRes()

data class MostCommonWordsVideoRes constructor(
    val videoId: String,
    val videoName: String
) : VideoFromRes()

data class AdvancedExercisesVideoRes constructor(
    val videoId: String,
    val videoName: String
) : VideoFromRes()

sealed class VideoFromRes