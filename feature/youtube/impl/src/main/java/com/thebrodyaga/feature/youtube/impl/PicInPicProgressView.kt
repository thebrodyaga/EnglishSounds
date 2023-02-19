package com.thebrodyaga.feature.youtube.impl

import android.content.Context
import android.util.AttributeSet
import android.widget.ProgressBar
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

class PicInPicProgressView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ProgressBar(context, attrs, defStyleAttr) {

    fun setUpView(youTubePlayerView: YouTubePlayerView) {
        youTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onVideoDuration(youTubePlayer: YouTubePlayer, duration: Float) {
                max = duration.toInt()
            }

            override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
                progress = second.toInt()
            }

            override fun onStateChange(
                youTubePlayer: YouTubePlayer,
                state: PlayerConstants.PlayerState
            ) {
                if (state == PlayerConstants.PlayerState.UNSTARTED) {
                    max = 0
                    progress = 0
                }
            }
        })
    }
}