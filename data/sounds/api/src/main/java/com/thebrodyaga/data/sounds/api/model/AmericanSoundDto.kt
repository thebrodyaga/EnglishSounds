package com.thebrodyaga.data.sounds.api.model

import com.google.gson.annotations.SerializedName

sealed interface SoundDto

data class AmericanSoundDto constructor(
    val transcription: String,
    val name: String,
    val description: String,
    val audioPath: String,
    val photoPath: String,
    val spellingWordList: List<SpellingWordDto>,
    val soundPracticeWords: SoundPracticeWords,
    val soundType: SoundType
) : SoundDto

data class SoundPracticeWords constructor(
    val beginningSound: List<PracticeWordDto>,
    val endSound: List<PracticeWordDto>,
    val middleSound: List<PracticeWordDto>
)

enum class SoundType {
    @SerializedName("consonantSounds")
    CONSONANT_SOUND,
    @SerializedName("rControlledVowels")
    R_CONTROLLED_VOWELS,
    @SerializedName("vowelSounds")
    VOWEL_SOUNDS;
}