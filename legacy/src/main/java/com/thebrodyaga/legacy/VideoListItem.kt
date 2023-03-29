package com.thebrodyaga.legacy

import androidx.annotation.StringRes
import com.thebrodyaga.data.sounds.api.model.SoundType
import com.thebrodyaga.feature.videoList.api.VideoListType

sealed class VideoListItem constructor(
    @StringRes val title: Int,
    @StringRes val shortTitle: Int,
    open val list: List<VideoItem>
) : SoundsListItem

data class ContrastingSoundVideoListItem constructor(
    override val list: List<VideoItem>
) : VideoListItem(
    R.string.contrasting_sound_video_title,
    R.string.contrasting_sound_video_title,
    list
)

data class MostCommonWordsVideoListItem constructor(
    override val list: List<VideoItem>
) : VideoListItem(
    R.string.most_common_words_video_title,
    R.string.most_common_words_video_title,
    list
)

data class AdvancedExercisesVideoListItem constructor(
    override val list: List<VideoItem>
) : VideoListItem(
    R.string.advanced_exercises_video_title,
    R.string.advanced_exercises_video_title,
    list
)

data class SoundVideoListItem constructor(
    val soundType: SoundType,
    override val list: List<VideoItem>
) : VideoListItem(soundType.humanName(), soundType.shortHumanName(), list)

fun VideoListType.titleRes(): Int = when (this) {
    VideoListType.ContrastingSounds -> R.string.contrasting_sound_video_title
    VideoListType.MostCommonWords -> R.string.most_common_words_video_title
    VideoListType.AdvancedExercises -> R.string.advanced_exercises_video_title
    VideoListType.VowelSounds -> R.string.vowel_sounds_short
    VideoListType.RControlledVowels -> R.string.r_controlled_vowels_short
    VideoListType.ConsonantSounds -> R.string.consonant_sounds_short
}