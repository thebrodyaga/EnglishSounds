package com.thebrodyaga.englishsounds.youtube

import android.annotation.SuppressLint
import android.app.PictureInPictureParams
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Rational
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.isVisible
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.ui.views.YouTubePlayerSeekBar
import com.thebrodyaga.englishsounds.R
import kotlinx.android.synthetic.main.activity_youtube_player.*
import kotlinx.android.synthetic.main.ayp_default_player_ui.*
import kotlinx.android.synthetic.main.ayp_default_player_ui.view.*
import java.lang.IllegalArgumentException

class YoutubePlayerActivity : AppCompatActivity() {

    private var currentSecond = 0f
    private var youTubePlayer: YouTubePlayer? = null
    private val orientationListener = OrientationListener()
    private var orientationEventListener: OrientationEventListener? = null

    private lateinit var videoId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_youtube_player)

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

        orientationEventListener = YoutubeOrientationEventListener(this)
            .also { it.enable() }

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

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        orientationListener.onConfigurationChanged(newConfig, this)
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
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration?
    ) {
        youtube_player.getPlayerUiController().showUi(!isInPictureInPictureMode)
    }

    override fun onDestroy() {
        super.onDestroy()
        orientationEventListener?.disable()
    }

    private fun enterPicInPic() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
            packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)
        ) enterPictureInPictureMode(
            with(PictureInPictureParams.Builder()) {
                val width = 16
                val height = 9
                setAspectRatio(Rational(width, height))
                build()
            })
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
        pic_in_pic_button.isVisible = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
                packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)
        pic_in_pic_button.setOnClickListener { enterPicInPic() }

        val youtubeViewPanel = youtube_player.panel
        val youtubeProgress = youtube_player.youtube_player_seekbar
        val mDetector =
            GestureDetectorCompat(this, GestureListener(youtubeViewPanel, youtubeProgress))
        youtubeViewPanel.setOnTouchListener { _, event -> mDetector.onTouchEvent(event) }
    }

    private inner class YouTubePlayerListener : AbstractYouTubePlayerListener() {

        override fun onReady(youTubePlayer: YouTubePlayer) {
            this@YoutubePlayerActivity.youTubePlayer = youTubePlayer
            youTubePlayer.loadVideo(videoId, currentSecond)
        }

        override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
            currentSecond = second
        }
    }

    private inner class GestureListener(
        private val panel: View,
        private val progress: YouTubePlayerSeekBar
    ) : GestureDetector.SimpleOnGestureListener() {

        override fun onDoubleTap(e: MotionEvent?): Boolean {
            e ?: return false
            val oneThird = panel.width / 3
            val newSecond = when (panel.left + e.x.toInt()) {
                in panel.left..(panel.left + oneThird) ->
                    currentSecond - DEFAULT_REWIND_S
                in (panel.right - oneThird)..panel.right ->
                    currentSecond + DEFAULT_FAST_FORWARD_S
                else -> return false
            }
            currentSecond = newSecond
            youTubePlayer?.apply {
                seekTo(currentSecond)
                progress.onCurrentSecond(this, currentSecond)
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

        private fun valueInRange(value: Int, center: Int, range: Int): Boolean {
            return value >= center - range && value <= center + range
        }

        fun startActivity(context: Context, videoId: String) {
            val intent = Intent(context, YoutubePlayerActivity::class.java).apply {
                putExtra(VIDEO_ID_EXTRA, videoId)
            }
            context.startActivity(intent)
        }
    }
}
