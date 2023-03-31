package com.thebrodyaga.brandbook.component.play

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.RippleDrawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.drawable.DrawableCompat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.google.android.material.ripple.RippleUtils
import com.thebrodyaga.brandbook.R
import com.thebrodyaga.core.uiUtils.common.getColorStateList
import com.thebrodyaga.core.uiUtils.drawable.shapeDrawable
import com.thebrodyaga.core.uiUtils.ripple.rippleForeground
import com.thebrodyaga.core.uiUtils.shape.shapeCircle

class PlayButtonView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatImageView(context, attrs) {

    private val pauseToPlay = AnimatedVectorDrawableCompat.create(context, R.drawable.ic_pause_to_play)
    private val playToPause = AnimatedVectorDrawableCompat.create(context, R.drawable.ic_play_to_pause)
    private var state: State = State.PLAY

    init {
        playToPause?.setTintList(getColorStateList(R.attr.colorPrimary))
        pauseToPlay?.setTintList(getColorStateList(R.attr.colorPrimary))
        setImageDrawable(playToPause)
        rippleForeground(shapeCircle())
    }

    fun togglePlaying() {
        when (state) {
            State.PLAY -> playToPause()
            State.PAUSE -> pauseToPlay()
        }
    }

    fun forcePause() {
        setImageDrawable(pauseToPlay)
        state = State.PAUSE
    }

    fun forcePlay() {
        setImageDrawable(playToPause)
        state = State.PLAY
    }

    fun playToPause() {
        if (state == State.PAUSE) return
        if (!isLaidOut) {
            forcePause()
            return
        }
        setImageDrawable(playToPause?.apply { start() })
        state = State.PAUSE
    }

    fun pauseToPlay() {
        if (state == State.PLAY) return
        if (!isLaidOut) {
            forcePlay()
            return
        }
        setImageDrawable(pauseToPlay?.apply { start() })
        state = State.PLAY
    }

    private enum class State {
        PLAY, PAUSE
    }

    private fun createBackground(): Drawable? {
        val backgroundDrawable = shapeDrawable()
        val context: Context = getContext()
        backgroundDrawable.initializeElevationOverlay(context)
        DrawableCompat.setTintList(backgroundDrawable, getColorStateList(R.attr.colorSurfaceContainerHigh))
        if (backgroundTintMode != null) {
            DrawableCompat.setTintMode(backgroundDrawable, backgroundTintMode)
        }
        val surfaceColorStrokeDrawable = shapeDrawable()
        surfaceColorStrokeDrawable.setTint(Color.TRANSPARENT)
        val maskDrawable = shapeDrawable()
        DrawableCompat.setTint(maskDrawable, Color.WHITE)
        val rippleDrawable = RippleDrawable(
            RippleUtils.sanitizeRippleDrawableColor(getColorStateList(R.attr.rippleColor)),
            LayerDrawable(arrayOf<Drawable>(surfaceColorStrokeDrawable, backgroundDrawable)),
            maskDrawable
        )
        return rippleDrawable
    }
}