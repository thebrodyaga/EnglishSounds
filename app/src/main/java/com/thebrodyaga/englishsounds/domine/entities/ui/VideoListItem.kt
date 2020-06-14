package com.thebrodyaga.englishsounds.domine.entities.ui

import androidx.annotation.StringRes
import com.thebrodyaga.englishsounds.R


sealed class VideoListItem constructor(
    @StringRes val title: Int,
    open val list: List<VideoItem>
) : SoundsListItem

data class ContrastingSoundVideoListItem constructor(
    override val list: List<VideoItem>
) : VideoListItem(R.string.contrasting_sound_video_title, list)