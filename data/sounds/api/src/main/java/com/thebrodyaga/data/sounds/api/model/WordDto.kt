package com.thebrodyaga.data.sounds.api.model

interface WordDto {
    val sound: String
    val name: String
    val audioPath: String
}

data class SpellingWordDto constructor(
    override val sound: String,
    override val name: String,
    override val audioPath: String,
    var transcription: String
) : WordDto

data class PracticeWordDto constructor(
    override val sound: String,
    override val name: String,
    override val audioPath: String,
    val soundPositionType: SoundPositionType
) : WordDto

enum class SoundPositionType {
    BEGINNING_SOUND,
    MIDDLE_SOUND,
    END_SOUND
}