package com.thebrodyaga.legacy

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.thebrodyaga.legacy.data.AdTag
import com.thebrodyaga.data.sounds.api.model.AmericanSoundDto
import com.thebrodyaga.data.sounds.api.model.SoundType

data class SoundHeader constructor(
    var soundType: SoundType
) : SoundsListItem

data class WordsHeader(
    var type: Type
) : SoundsDetailsListItem {
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

data class ShowMore(
    var key: String = "ShowMore"
) : SoundsDetailsListItem

data class AdItem constructor(
    val adTag: AdTag,
    val customTag: String? = null
) : SoundsListItem, VideoItemInList

data class ShortAdItem constructor(
    val adTag: AdTag,
    val customTag: String? = null
) : SoundsListItem, VideoItemInList, SoundsDetailsListItem

data class SoundsDetailsWithAd constructor(
    val sound: AmericanSoundDto,
    val shortAdItem: ShortAdItem
) : SoundsDetailsListItem

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
