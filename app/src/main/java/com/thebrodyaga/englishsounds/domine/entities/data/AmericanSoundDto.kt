package com.thebrodyaga.englishsounds.domine.entities.data

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.google.gson.annotations.SerializedName
import com.thebrodyaga.englishsounds.R
import com.thebrodyaga.englishsounds.domine.entities.ui.SoundsDetailsListItem
import com.thebrodyaga.englishsounds.domine.entities.ui.SoundsListItem

interface SoundDto

data class AmericanSoundDto constructor(
    var transcription: String,
    var name: String,
    var description: String,
    var audioPath: String,
    var photoPath: String,
    var spellingWordList: List<SpellingWordDto>,
    var soundPracticeWords: SoundPracticeWords,
    var soundType: SoundType
) : SoundDto, SoundsListItem, SoundsDetailsListItem

data class SoundPracticeWords constructor(
    var beginningSound: List<PracticeWordDto>,
    var endSound: List<PracticeWordDto>,
    var middleSound: List<PracticeWordDto>
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
}