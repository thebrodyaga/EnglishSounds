package com.thebrodyaga.englishsounds.youtube

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.thebrodyaga.englishsounds.R
import kotlinx.android.synthetic.main.activity_youtube_player.*
import kotlinx.android.synthetic.main.ayp_default_player_ui.*
import kotlinx.android.synthetic.main.ayp_default_player_ui.view.*
import timber.log.Timber

class YoutubePlayerActivity : AppCompatActivity() {

    private var currentSecond = 0f
    private var youTubePlayer: YouTubePlayer? = null
    private var playerState: PlayerConstants.PlayerState = PlayerConstants.PlayerState.UNKNOWN

    private lateinit var orientationListener: OrientationListener
    private lateinit var orientationEventListener: OrientationEventListener

    private var picInPicReceiver: PicInPicReceiver? = null

    private lateinit var videoId: String
    private lateinit var picInPickHelper: PicInPickHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_youtube_player)

        picInPickHelper = PicInPickHelper(this)
        orientationListener = OrientationListener(this)
        orientationEventListener = YoutubeOrientationEventListener(this)
            .also { it.enable() }

        videoId = intent.getStringExtra(VIDEO_ID_EXTRA)
            ?: throw IllegalArgumentException("need put videoID")

        val legacyYouTubePlayerView = youtube_player.panel.parent.parent as ViewGroup

        root_view.setOnApplyWindowInsetsListener { v, insets ->
            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
                v.setPadding(
                    insets.systemWindowInsetLeft,
                    insets.systemWindowInsetTop,
                    insets.systemWindowInsetRight,
                    insets.systemWindowInsetBottom
                )
            else v.setPadding(0, 0, 0, 0)
            insets
        }

        lifecycle.addObserver(youtube_player)
        youtube_player.addYouTubePlayerListener(YouTubePlayerListener())
        youtube_player.getPlayerUiController()
            .setFullScreenButtonClickListener(View.OnClickListener { toggleFullScreen() })

        youtube_player
            .addFullScreenListener(FullScreenListener(youtube_player, legacyYouTubePlayerView))

        setUpUiController()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val newVideoId = intent?.getStringExtra(VIDEO_ID_EXTRA)
            ?: return
        if (newVideoId != videoId) {
            videoId = newVideoId
            youTubePlayer?.loadVideo(videoId, 0f)
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            orientationListener.onConfigurationChanged(resources.configuration)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        orientationListener.onConfigurationChanged(newConfig)
        when (newConfig.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> youtube_player.exitFullScreen()
            Configuration.ORIENTATION_LANDSCAPE -> youtube_player.enterFullScreen()
        }
    }

    // Picture in Picture related functions.
    override fun onUserLeaveHint() {
//        enterPicInPic()
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean, newConfig: Configuration?
    ) {
        youtube_player.getPlayerUiController()
            .showUi(!isInPictureInPictureMode)
        pic_in_pic_progress.isInvisible = !isInPictureInPictureMode
        if (isInPictureInPictureMode)
            picInPicReceiver = PicInPicReceiver { onPipControlReceive(it) }
                .also { registerReceiver(it, IntentFilter(ACTION_MEDIA_CONTROL)) }
        else picInPicReceiver?.let {
            unregisterReceiver(it)
            picInPicReceiver = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        orientationEventListener.disable()
    }

    private fun onPipControlReceive(controlType: @PicInPicControlType Int) {
        youTubePlayer?.apply {
            when (controlType) {
                PIP_CONTROL_TYPE_PLAY -> if (playerState != PlayerConstants.PlayerState.BUFFERING) play()
                PIP_CONTROL_TYPE_PAUSE -> pause()
                PIP_CONTROL_TYPE_FAST_FORWARD -> this@YoutubePlayerActivity.seekTo(true)
                PIP_CONTROL_TYPE_REWIND -> this@YoutubePlayerActivity.seekTo(false)
                PIP_CONTROL_TYPE_REPEAT -> seekTo(0f)
            }
        }
    }

    fun seekTo(isFastForward: Boolean) {
        val newSecond = if (!isFastForward)
            currentSecond - DEFAULT_REWIND_S
        else currentSecond + DEFAULT_FAST_FORWARD_S
        currentSecond = newSecond
        youTubePlayer?.apply {
            seekTo(currentSecond)
            youtube_player?.youtube_player_seekbar?.onCurrentSecond(this, currentSecond)
            pic_in_pic_progress.progress = currentSecond.toInt()
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private fun toggleFullScreen() {
        when (resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> requestedOrientation =
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            Configuration.ORIENTATION_LANDSCAPE -> requestedOrientation =
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    private fun setUpUiController() {
        pic_in_pic_button.isVisible = picInPickHelper.isHavePicInPicMode()
        pic_in_pic_button.setOnClickListener { picInPickHelper.enterPicInPic(this) }

        val youtubeViewPanel = youtube_player.panel
        val mDetector =
            GestureDetectorCompat(this, GestureListener(youtubeViewPanel))
        youtubeViewPanel.setOnTouchListener { _, event -> mDetector.onTouchEvent(event) }
    }

    private inner class YouTubePlayerListener : AbstractYouTubePlayerListener() {

        override fun onVideoDuration(youTubePlayer: YouTubePlayer, duration: Float) {
            super.onVideoDuration(youTubePlayer, duration)
            pic_in_pic_progress.max = duration.toInt()
        }

        override fun onReady(youTubePlayer: YouTubePlayer) {
            this@YoutubePlayerActivity.youTubePlayer = youTubePlayer
            youTubePlayer.loadVideo(videoId, currentSecond)
        }

        override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
            currentSecond = second
            pic_in_pic_progress.progress = second.toInt()
        }

        override fun onStateChange(
            youTubePlayer: YouTubePlayer,
            state: PlayerConstants.PlayerState
        ) {
            playerState = state
            if (state == PlayerConstants.PlayerState.UNSTARTED) {
                pic_in_pic_progress.max = 0
                pic_in_pic_progress.progress = 0
            }
            if (!picInPickHelper.isHavePicInPicMode())
                return
            Timber.i(state.toString())
            setPicInPicBuilderByPlayerState(state)
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun setPicInPicBuilderByPlayerState(state: PlayerConstants.PlayerState) {
        val picInPicBuilder = when (state) {
            PlayerConstants.PlayerState.BUFFERING,
            PlayerConstants.PlayerState.PLAYING ->
                picInPickHelper.actionsForRunning(isPlaying = true, setSeekTo = true)

            PlayerConstants.PlayerState.VIDEO_CUED,
            PlayerConstants.PlayerState.PAUSED ->
                picInPickHelper.actionsForRunning(isPlaying = false, setSeekTo = true)

            PlayerConstants.PlayerState.UNSTARTED ->
                picInPickHelper.actionsForRunning(false)

            PlayerConstants.PlayerState.UNKNOWN -> picInPickHelper.actionsEmpty()
            PlayerConstants.PlayerState.ENDED -> picInPickHelper.actionsForEnd()
        }
        setPictureInPictureParams(picInPicBuilder.build())
    }

    private inner class GestureListener(private val panel: View) :
        GestureDetector.SimpleOnGestureListener() {

        override fun onDoubleTap(e: MotionEvent?): Boolean {
            e ?: return false
            val oneThird = panel.width / 3
            when (panel.left + e.x.toInt()) {
                in panel.left..(panel.left + oneThird) ->
                    seekTo(false)
                in (panel.right - oneThird)..panel.right ->
                    seekTo(true)
                else -> return false
            }
            return true
        }
    }

    companion object {

        /** The default fast forward increment, in seconds.  */
        private const val DEFAULT_FAST_FORWARD_S = 15
        /** The default rewind increment, in seconds.  */
        private const val DEFAULT_REWIND_S = 5

        private const val VIDEO_ID_EXTRA = "VIDEO_ID_EXTRA"

        fun startActivity(context: Context, videoId: String) {
            val intent = Intent(context, YoutubePlayerActivity::class.java).apply {
                putExtra(VIDEO_ID_EXTRA, videoId)
            }
            context.startActivity(intent)
        }
    }
}
