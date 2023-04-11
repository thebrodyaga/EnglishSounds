package com.thebrodyaga.legacy

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.net.toUri
import androidx.core.view.doOnLayout
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.thebrodyaga.legacy.databinding.ViewYoutubeThumbnailBinding

class YoutubeThumbnailView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    val binding by viewBinding(ViewYoutubeThumbnailBinding::bind)

    init {
        inflate(context, R.layout.view_youtube_thumbnail, this)
    }

    fun loadYoutubeThumbnail(videoId: String) = doOnLayout {
        Glide.with(context)
            .load(getThumbnailUtl(videoId))
            .override(it.width, it.height)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .centerCrop()
            .into(binding.youtubeThumbnail)
    }


    private fun getThumbnailUtl(videoId: String) =
        "https://img.youtube.com/vi/$videoId/maxresdefault.jpg".toUri()
}