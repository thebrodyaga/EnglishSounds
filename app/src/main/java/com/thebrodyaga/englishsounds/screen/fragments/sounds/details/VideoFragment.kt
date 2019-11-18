package com.thebrodyaga.englishsounds.screen.fragments.sounds.details

import android.os.Bundle
import android.view.View
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.thebrodyaga.englishsounds.R
import com.thebrodyaga.englishsounds.screen.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_video.*


class VideoFragment : BaseFragment() {
    override fun getLayoutId(): Int = R.layout.fragment_video

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycle.addObserver(youtube_player)
        youtube_player.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                super.onReady(youTubePlayer)
                val videoId = arguments?.getString(EXTRA_KEY)
                    ?: throw IllegalArgumentException("need put video")
                youTubePlayer.loadVideo(videoId, 0f)
                youTubePlayer.pause()
            }
        })
    }

    companion object {
        private const val EXTRA_KEY = "EXTRA_KEY"
        fun newInstance(video: String): VideoFragment = VideoFragment()
            .apply { arguments = Bundle().also { it.putString(EXTRA_KEY, video) } }
    }
}
