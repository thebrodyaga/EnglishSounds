package com.thebrodyaga.englishsounds.screen.view

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.bumptech.glide.Glide
import com.thebrodyaga.englishsounds.R
import kotlinx.android.synthetic.main.view_youtube_thumbnail.view.*

class YoutubeThumbnailView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        inflate(context, R.layout.view_youtube_thumbnail, this)
    }

    fun loadYoutubeThumbnail(videoId: String) {
        Glide.with(context)
            .load(getThumbnailUtl(videoId))
            .into(youtube_thumbnail)
    }

    private fun getThumbnailUtl(videoId: String) =
        "https://img.youtube.com/vi/$videoId/maxresdefault.jpg"
}