package com.thebrodyaga.englishsounds.domine.entities.ui

import androidx.annotation.StringRes
import com.thebrodyaga.englishsounds.R
import com.thebrodyaga.englishsounds.domine.entities.data.SoundType

data class SoundHeader(
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

interface SoundsListItem
interface SoundsDetailsListItem