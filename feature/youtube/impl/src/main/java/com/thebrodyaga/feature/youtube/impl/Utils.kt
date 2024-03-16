package com.thebrodyaga.feature.youtube.impl

import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Handler
import android.view.OrientationEventListener
import android.view.View
import android.view.ViewGroup
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.FullscreenListener
import kotlin.math.abs

class YoutubeOrientationEventListener constructor(
    private val activity: Activity
) : OrientationEventListener(activity) {

    private val handler = Handler(activity.mainLooper)

    override fun onOrientationChanged(orientation: Int) {
        val epsilon = 10
        val leftLandscape = 90
        val rightLandscape = 270

        fun epsilonCheck(a: Int, b: Int, epsilon: Int): Boolean {
            return abs(a - b) < epsilon
        }

        if (epsilonCheck(orientation, leftLandscape, epsilon) ||
            epsilonCheck(orientation, rightLandscape, epsilon)
        ) {
            handler.postDelayed({
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            }, 300)
        } else handler.removeCallbacksAndMessages(null)
    }

    override fun disable() {
        super.disable()
        handler.removeCallbacksAndMessages(null)
    }
}

class OrientationListener constructor(private val activity: Activity) {
    private var oldOrientation = -1

    fun onConfigurationChanged(newConfig: Configuration) {
        if (newConfig.orientation != oldOrientation) {
            oldOrientation = newConfig.orientation
            onOrientationChanged(oldOrientation, activity)
        }
    }

    private fun onOrientationChanged(orientation: Int, activity: Activity) {
        when (orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> toggleFullscreen(true, activity)
            Configuration.ORIENTATION_PORTRAIT -> toggleFullscreen(false, activity)
        }
    }

    private fun toggleFullscreen(isFullscreen: Boolean, activity: Activity) {
        activity.window.decorView.systemUiVisibility =
            if (isFullscreen) {
                (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
            } else {
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            }
    }
}

class YouTubePlayerFullScreenListener constructor(
    private val youTubePlayerView: ViewGroup,
    private val legacyYouTubePlayerView: ViewGroup
) : FullscreenListener {


    override fun onEnterFullscreen(fullscreenView: View, exitFullscreen: () -> Unit) {
    }

    override fun onExitFullscreen() {
        youTubePlayerView.layoutParams = youTubePlayerView.layoutParams.apply {
            height = ViewGroup.LayoutParams.MATCH_PARENT
            width = ViewGroup.LayoutParams.MATCH_PARENT
        }

        legacyYouTubePlayerView.layoutParams = legacyYouTubePlayerView.layoutParams.apply {
            height = ViewGroup.LayoutParams.MATCH_PARENT
            width = ViewGroup.LayoutParams.MATCH_PARENT
        }
    }

}
