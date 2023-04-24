package com.thebrodyaga.legacy

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.thebrodyaga.data.sounds.api.model.SoundType

data class WordsHeader(
    var type: Type
) {
    enum class Type {
        BEGINNING_SOUND,
        MIDDLE_SOUND,
        END_SOUND,
        SPELLING;

        @StringRes
        fun humanName(): Int = when (this) {
            BEGINNING_SOUND -> R.string.BEGINNING_SOUND
            MIDDLE_SOUND -> R.string.MIDDLE_SOUND
            END_SOUND -> R.string.END_SOUND
            SPELLING -> R.string.SPELLING
        }
    }
}

interface SoundsListItem
interface SoundsDetailsListItem

@ColorRes
fun SoundType.color(): Int = when (this) {
    SoundType.CONSONANT_SOUND -> R.color.consonant_sounds
    SoundType.R_CONTROLLED_VOWELS -> R.color.r_controlled_vowels
    SoundType.VOWEL_SOUNDS -> R.color.vowel_sounds
}

@StringRes
fun SoundType.humanName(): Int = when (this) {
    SoundType.CONSONANT_SOUND -> R.string.consonant_sounds
    SoundType.R_CONTROLLED_VOWELS -> R.string.r_controlled_vowels
    SoundType.VOWEL_SOUNDS -> R.string.vowel_sounds
}

@StringRes
fun SoundType.shortHumanName(): Int = when (this) {
    SoundType.CONSONANT_SOUND -> R.string.consonant_sounds_short
    SoundType.R_CONTROLLED_VOWELS -> R.string.r_controlled_vowels_short
    SoundType.VOWEL_SOUNDS -> R.string.vowel_sounds_short
}
