package com.thebrodyaga.legacy

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.thebrodyaga.legacy.databinding.ViewYoutubeThumbnailBinding

class YoutubeThumbnailView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    val binding by viewBinding(ViewYoutubeThumbnailBinding::bind)

    init {
        inflate(context, R.layout.view_youtube_thumbnail, this)
    }

    fun loadYoutubeThumbnail(videoId: String) {
        Glide.with(context)
            .load(getThumbnailUtl(videoId))
            .into(binding.youtubeThumbnail)
    }



    private fun getThumbnailUtl(videoId: String) =
        "https://img.youtube.com/vi/$videoId/maxresdefault.jpg"
}