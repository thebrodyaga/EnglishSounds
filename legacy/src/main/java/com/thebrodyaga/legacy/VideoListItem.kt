package com.thebrodyaga.legacy

import androidx.annotation.StringRes
import com.thebrodyaga.data.sounds.api.model.SoundType

sealed class VideoListItem constructor(
    @StringRes val title: Int,
    @StringRes val shortTitle: Int,
    open val list: List<VideoItemInList>
) : SoundsListItem

data class ContrastingSoundVideoListItem constructor(
    override val list: List<VideoItemInList>
) : VideoListItem(
    R.string.contrasting_sound_video_title,
    R.string.contrasting_sound_video_title,
    list
)

data class MostCommonWordsVideoListItem constructor(
    override val list: List<VideoItemInList>
) : VideoListItem(
    R.string.most_common_words_video_title,
    R.string.most_common_words_video_title,
    list
)

data class AdvancedExercisesVideoListItem constructor(
    override val list: List<VideoItemInList>
) : VideoListItem(
    R.string.advanced_exercises_video_title,
    R.string.advanced_exercises_video_title,
    list
)

data class SoundVideoListItem constructor(
    val soundType: SoundType,
    override val list: List<VideoItemInList>
) : VideoListItem(soundType.humanName(), soundType.shortHumanName(), list)