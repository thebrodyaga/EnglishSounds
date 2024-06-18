package com.thebrodyaga.feature.youtube.impl

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.OrientationEventListener
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.children
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.firebase.analytics.FirebaseAnalytics
import com.pierfrancescosoffritti.androidyoutubeplayer.core.customui.DefaultPlayerUiController
import com.pierfrancescosoffritti.androidyoutubeplayer.core.customui.views.YouTubePlayerSeekBar
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.thebrodyaga.core.uiUtils.resources.px
import com.thebrodyaga.englishsounds.analytics.AnalyticsEngine
import com.thebrodyaga.englishsounds.analytics.AppAnalytics
import com.thebrodyaga.englishsounds.base.app.BaseActivity
import com.thebrodyaga.englishsounds.base.app.ViewModelFactory
import com.thebrodyaga.englishsounds.base.di.findDependencies
import com.thebrodyaga.feature.youtube.api.PlayVideoExtra
import com.thebrodyaga.feature.youtube.impl.databinding.ActivityYoutubePlayerBinding
import com.thebrodyaga.feature.youtube.impl.di.YoutubeActivityComponent
import timber.log.Timber
import javax.inject.Inject


class YoutubePlayerActivity : BaseActivity() {

    private var currentSecond: Float
        get() = viewModel.currentSecond
        set(value) {
            viewModel.currentSecond = value
        }

    private var playerState: PlayerConstants.PlayerState
        get() = viewModel.playerState
        set(value) {
            viewModel.playerState = value
        }

    private var youTubePlayer: YouTubePlayer? = null

    private lateinit var orientationListener: OrientationListener
    private lateinit var orientationEventListener: OrientationEventListener

    private var picInPicReceiver: PicInPicReceiver? = null

    private lateinit var playVideoExtra: PlayVideoExtra

    @RequiresApi(Build.VERSION_CODES.O)
    private lateinit var picInPickHelper: PicInPickHelper

    private val binding by viewBinding(ActivityYoutubePlayerBinding::bind)

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: YoutubePlayerViewModel by viewModels { viewModelFactory }


    private lateinit var youtube_player_seekbar: YouTubePlayerSeekBar
    private lateinit var panel: View
    private lateinit var pic_in_pic_button: ImageView
    private var playerUiController: DefaultPlayerUiController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        YoutubeActivityComponent.factory(findDependencies()).inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_youtube_player)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            picInPickHelper = PicInPickHelper(this)
        }
        orientationListener = OrientationListener(this)
        orientationEventListener = YoutubeOrientationEventListener(this)
            .also { it.enable() }

        playVideoExtra = intent.getSerializableExtra(VIDEO_ID_EXTRA) as? PlayVideoExtra
            ?: throw IllegalArgumentException("need put videoID")

        val legacyYouTubePlayerView = binding.youtubePlayer.children.first() as ViewGroup

        binding.rootView.setOnApplyWindowInsetsListener { v, insets ->
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

        lifecycle.addObserver(binding.youtubePlayer)
        binding.picInPicProgress.setUpView(binding.youtubePlayer)
        val options: IFramePlayerOptions = IFramePlayerOptions.Builder()
            .controls(0).build()
        binding.youtubePlayer.initialize(YouTubePlayerListener(), options)
        binding.youtubePlayer.addFullscreenListener(
            YouTubePlayerFullScreenListener(
                binding.youtubePlayer,
                legacyYouTubePlayerView
            )
        )
        addOnPictureInPictureModeChangedListener {
            onPicInPicModeChanged(it.isInPictureInPictureMode)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val newVideoId = intent?.getSerializableExtra(VIDEO_ID_EXTRA) as PlayVideoExtra
            ?: return
        val videoId = newVideoId.videoId
        if (videoId != playVideoExtra.videoId) {
            playVideoExtra = newVideoId
            playVideoExtra.videoId
            currentSecond = 0f
            youTubePlayer?.loadVideo(videoId, 0f)
        } else youTubePlayer?.loadVideo(videoId, currentSecond)
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
//            Configuration.ORIENTATION_PORTRAIT -> binding.youtubePlayer.exitFullScreen()
//            Configuration.ORIENTATION_LANDSCAPE -> binding.youtubePlayer.enterFullScreen()
        }
    }

    // Picture in Picture related functions.
    override fun onUserLeaveHint() {
//        enterPicInPic()
    }

    private fun onPicInPicModeChanged(isInPictureInPictureMode: Boolean) {
        playerUiController?.showUi(!isInPictureInPictureMode)
        binding.picInPicProgress.isInvisible = !isInPictureInPictureMode
        if (isInPictureInPictureMode)
            picInPicReceiver = PicInPicReceiver {
                onPipControlReceive(it)
            }.also {
                ContextCompat.registerReceiver(
                    this,
                    it,
                    IntentFilter(ACTION_MEDIA_CONTROL),
                    ContextCompat.RECEIVER_EXPORTED
                )
            }
        else picInPicReceiver?.let {
            unregisterReceiver(it)
            picInPicReceiver = null
            //huck for detect close pic in pic
            if (lifecycle.currentState == Lifecycle.State.CREATED)
                finishAndRemoveTask()
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
            youtube_player_seekbar.onCurrentSecond(this, currentSecond)
            binding.picInPicProgress.progress = currentSecond.toInt()
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
        pic_in_pic_button.isVisible = isHavePicInPicMode()
        pic_in_pic_button.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                picInPickHelper.enterPicInPic(this)
            }
        }

        val youtubeViewPanel = panel
        val mDetector = GestureDetectorCompat(this, GestureListener(youtubeViewPanel))
        youtubeViewPanel.setOnTouchListener { _, event -> mDetector.onTouchEvent(event) }
    }

    private inner class YouTubePlayerListener : AbstractYouTubePlayerListener() {

        override fun onReady(youTubePlayer: YouTubePlayer) {
            val context = this@YoutubePlayerActivity
            this@YoutubePlayerActivity.youTubePlayer = youTubePlayer
            val defaultPlayerUiController = DefaultPlayerUiController(binding.youtubePlayer, youTubePlayer)
            playerUiController = defaultPlayerUiController
            defaultPlayerUiController.setFullscreenButtonClickListener { toggleFullScreen() }
            val uiControllerView = defaultPlayerUiController.rootView
            binding.youtubePlayer.setCustomPlayerUi(defaultPlayerUiController.rootView)
            youtube_player_seekbar = uiControllerView.findViewById(R.id.youtube_player_seekbar)
            panel = uiControllerView.findViewById(R.id.panel)
            val youtubeBtn = uiControllerView.findViewById<ImageView>(R.id.youtube_button)
            uiControllerView.findViewById<ImageView>(R.id.play_pause_button).apply {
                imageTintList = ContextCompat.getColorStateList(context, R.color.youtube_color)
            }
            val linearLayout = youtube_player_seekbar.parent as ViewGroup
            val indexOfPicInPicIcon = linearLayout.indexOfChild(youtube_player_seekbar).inc()
            val picInPic = AppCompatImageView(context).apply {
                setImageResource(R.drawable.ic_picture_in_picture)
                imageTintList = youtubeBtn.imageTintList
                linearLayout.addView(this, indexOfPicInPicIcon, ViewGroup.LayoutParams(40.px, 40.px))
                scaleType = ImageView.ScaleType.CENTER_INSIDE
                pic_in_pic_button = this
            }
            setUpUiController()


            when (playerState) {
                PlayerConstants.PlayerState.PLAYING -> youTubePlayer.loadVideo(
                    playVideoExtra.videoId,
                    currentSecond
                )

                PlayerConstants.PlayerState.PAUSED -> youTubePlayer.cueVideo(
                    playVideoExtra.videoId,
                    currentSecond
                )

                else ->
                    youTubePlayer.loadVideo(playVideoExtra.videoId, currentSecond)
            }
        }

        override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
            currentSecond = second
        }

        override fun onStateChange(
            youTubePlayer: YouTubePlayer,
            state: PlayerConstants.PlayerState,
        ) {
            logVideoEvent(state, currentSecond)
            playerState = state
            if (!isHavePicInPicMode())
                return
            Timber.i(state.toString())
            setPicInPicBuilderByPlayerState(state)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setPicInPicBuilderByPlayerState(state: PlayerConstants.PlayerState) {
        val picInPicBuilder = when (state) {
            PlayerConstants.PlayerState.BUFFERING,
            PlayerConstants.PlayerState.PLAYING,
            ->
                picInPickHelper.actionsForRunning(isPlaying = true, setSeekTo = true)

            PlayerConstants.PlayerState.VIDEO_CUED,
            PlayerConstants.PlayerState.PAUSED,
            ->
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

        override fun onDoubleTap(e: MotionEvent): Boolean {
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

    private fun logVideoEvent(
        state: PlayerConstants.PlayerState,
        videoSecond: Float,
    ) {
        when (state) {
            PlayerConstants.PlayerState.ENDED,
            PlayerConstants.PlayerState.PLAYING,
            PlayerConstants.PlayerState.PAUSED,
            -> {
                val bundle = Bundle()
                bundle.putString(AppAnalytics.PARAM_VIDEO_NAME, playVideoExtra.videoName)
                bundle.putString(AppAnalytics.PARAM_VIDEO_STATE, state.toString())
                bundle.putInt(AppAnalytics.PARAM_VIDEO_DURATION, videoSecond.toInt())
                AnalyticsEngine.logEvent(AppAnalytics.EVENT_PLAY_VIDEO, bundle)
            }

            else -> return
        }
    }

    companion object {

        /** The default fast forward increment, in seconds.  */
        internal const val DEFAULT_FAST_FORWARD_S = 15

        /** The default rewind increment, in seconds.  */
        internal const val DEFAULT_REWIND_S = 5

        internal const val VIDEO_ID_EXTRA = "VIDEO_ID_EXTRA"

        fun startActivity(context: Context, playVideoExtra: PlayVideoExtra) {
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, playVideoExtra.videoId)
            bundle.putString(AppAnalytics.PARAM_VIDEO_NAME, playVideoExtra.videoName)
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "video")
            AnalyticsEngine.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
            val intent = Intent(context, YoutubePlayerActivity::class.java).apply {
                putExtra(VIDEO_ID_EXTRA, playVideoExtra)
            }
            context.startActivity(intent)
        }
    }
}
