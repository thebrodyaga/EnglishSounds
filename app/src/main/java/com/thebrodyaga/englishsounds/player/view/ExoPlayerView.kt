package com.thebrodyaga.englishsounds.player.view

import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.core.view.GestureDetectorCompat
import com.google.android.exoplayer2.ui.PlayerView

class ExoPlayerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : PlayerView(context, attrs, defStyleAttr) {

    init {
        val mDetector = GestureDetectorCompat(context, GestureListener())
        setOnTouchListener { _, event -> mDetector.onTouchEvent(event) }
    }

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onDoubleTap(e: MotionEvent?): Boolean {
            e ?: return false
            val oneThird = width / 3
            when (left + e.x.toInt()) {
                in left..(left + oneThird) ->
                    dispatchMediaKeyEvent(KeyEvent(e.action, KeyEvent.KEYCODE_MEDIA_REWIND))
                in (right - oneThird)..right ->
                    dispatchMediaKeyEvent(KeyEvent(e.action, KeyEvent.KEYCODE_MEDIA_FAST_FORWARD))
            }
            return true
        }
    }
}