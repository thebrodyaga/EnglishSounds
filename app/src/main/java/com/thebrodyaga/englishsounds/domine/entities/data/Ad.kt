package com.thebrodyaga.englishsounds.domine.entities.data

import androidx.annotation.StringRes
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.thebrodyaga.englishsounds.R
import com.thebrodyaga.englishsounds.domine.entities.ui.SoundsListItem
import com.thebrodyaga.englishsounds.domine.entities.ui.VideoItemInList

data class AdListBox constructor(
    val count: Int,
    val ads: List<AdBox>
)

data class AdBox constructor(
    val ad: UnifiedNativeAd? = null,
    val adTag: AdTag
)

enum class AdTag {

    SOUNDS_FIRST,
    SOUNDS_SECOND;

    @StringRes
    fun adUnitIdRes(): Int = when (this) {
        SOUNDS_FIRST -> R.string.native_sound_list
        SOUNDS_SECOND -> R.string.native_sound_list_second
    }
}