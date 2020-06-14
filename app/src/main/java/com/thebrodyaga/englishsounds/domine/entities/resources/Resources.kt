package com.thebrodyaga.englishsounds.domine.entities.resources

data class SoundVideoRes constructor(
    var transcription: String,
    val videoId: String
)

data class ContrastingSoundVideoRes constructor(
    var firstTranscription: String,
    var secondTranscription: String,
    val videoId: String,
    val videoName: String
)