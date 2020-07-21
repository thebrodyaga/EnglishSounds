package com.thebrodyaga.englishsounds.domine.entities.ui

import android.os.Parcelable
import androidx.annotation.StringRes
import com.thebrodyaga.englishsounds.R
import com.thebrodyaga.englishsounds.domine.entities.data.AdTag
import com.thebrodyaga.englishsounds.domine.entities.data.SoundType
import kotlinx.android.parcel.Parcelize

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

interface SoundsListItem
interface SoundsDetailsListItem

@Parcelize
data class PlayVideoExtra constructor(
    val videoId: String,
    val videoName: String
) : Parcelable