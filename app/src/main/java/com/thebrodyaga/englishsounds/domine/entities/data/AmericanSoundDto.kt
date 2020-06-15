package com.thebrodyaga.englishsounds.domine.entities.data

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.google.gson.annotations.SerializedName
import com.thebrodyaga.englishsounds.R
import com.thebrodyaga.englishsounds.domine.entities.ui.SoundsDetailsListItem
import com.thebrodyaga.englishsounds.domine.entities.ui.SoundsListItem

interface SoundDto

data class AmericanSoundDto constructor(
    val transcription: String,
    val name: String,
    val description: String,
    val audioPath: String,
    val photoPath: String,
    val spellingWordList: List<SpellingWordDto>,
    val soundPracticeWords: SoundPracticeWords,
    val soundType: SoundType
) : SoundDto, SoundsListItem, SoundsDetailsListItem

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

    @ColorRes
    fun color(): Int = when (this) {
        CONSONANT_SOUND -> R.color.consonant_sounds
        R_CONTROLLED_VOWELS -> R.color.r_controlled_vowels
        VOWEL_SOUNDS -> R.color.vowel_sounds
    }

    @StringRes
    fun humanName(): Int = when (this) {
        CONSONANT_SOUND -> R.string.consonant_sounds
        R_CONTROLLED_VOWELS -> R.string.r_controlled_vowels
        VOWEL_SOUNDS -> R.string.vowel_sounds
    }

    @StringRes
    fun shortHumanName(): Int = when (this) {
        CONSONANT_SOUND -> R.string.consonant_sounds_short
        R_CONTROLLED_VOWELS -> R.string.r_controlled_vowels_short
        VOWEL_SOUNDS -> R.string.vowel_sounds_short
    }
}