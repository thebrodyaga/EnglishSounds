package com.thebrodyaga.legacy.data

import androidx.annotation.StringRes
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.thebrodyaga.legacy.R

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
    SOUNDS_SECOND,
    SOUND_DETAILS,
    SOUND_DETAILS_SECOND,
    SOUND_DETAILS_THIRD,
    SOUND_DETAILS_FOURTH,
    SOUND_VIDEO_LIST,
    SOUND_LIST_OF_VIDEO_LIST,
    SOUND_TRAINING;

    @StringRes
    fun adUnitIdRes(): Int = when (this) {
        SOUNDS_FIRST -> R.string.native_sound_list
        SOUNDS_SECOND -> R.string.native_sound_list_second
        SOUND_DETAILS -> R.string.native_sound_details
        SOUND_DETAILS_SECOND -> R.string.native_sound_details_second
        SOUND_DETAILS_THIRD -> R.string.native_sound_details_third
        SOUND_DETAILS_FOURTH -> R.string.native_sound_details_fourth
        SOUND_VIDEO_LIST -> R.string.native_sound_video_list
        SOUND_LIST_OF_VIDEO_LIST -> R.string.native_sound_list_of_video_list
        SOUND_TRAINING -> R.string.native_sound_training
    }
}