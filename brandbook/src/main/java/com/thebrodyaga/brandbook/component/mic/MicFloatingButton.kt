package com.thebrodyaga.brandbook.component.mic

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.thebrodyaga.brandbook.R
import com.thebrodyaga.core.uiUtils.common.getColorStateList

class MicFloatingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FloatingActionButton(context, attrs) {

    private val pauseToPlay = AnimatedVectorDrawableCompat.create(context, R.drawable.ic_pause_to_play)
    private val playToPause = AnimatedVectorDrawableCompat.create(context, R.drawable.ic_play_to_pause)
    private val mic = ContextCompat.getDrawable(context, R.drawable.ic_mic)
    private val record = ContextCompat.getDrawable(context, R.drawable.ic_record)
    private val play = ContextCompat.getDrawable(context, R.drawable.ic_play)
    private val pause = ContextCompat.getDrawable(context, R.drawable.ic_pause)
    private val primaryTint = getColorStateList(R.attr.colorOnPrimaryContainer)
    private val tertiaryTint = getColorStateList(R.attr.colorOnTertiaryContainer)

    private val primaryContainerTint = getColorStateList(R.attr.colorPrimaryContainer)
    private val secondaryContainerTint = getColorStateList(R.attr.colorSecondaryContainer)
    private val tertiaryContainerTint = getColorStateList(R.attr.colorTertiaryContainer)

    private val redTint = ContextCompat.getColorStateList(context, R.color.error_object)
    private var state: State = State.PLAY

    init {
        imageTintList = primaryTint
        setImageDrawable(pauseToPlay)
        setImageDrawable(playToPause)
        setImageDrawable(mic)
    }

    fun forcePause() {
        if (state == State.FORCE_PAUSE) return
        imageTintList = tertiaryTint
        backgroundTintList = tertiaryContainerTint
        setImageDrawable(pause)
        state = State.FORCE_PAUSE
    }

    fun forcePlay() {
        if (state == State.FORCE_PLAY) return
        imageTintList = tertiaryTint
        backgroundTintList = tertiaryContainerTint
        setImageDrawable(play)
        state = State.FORCE_PLAY
    }

    fun mic() {
        setImageDrawable(mic)
        imageTintList = primaryTint
        backgroundTintList = primaryContainerTint
        state = State.MIC
    }

    fun record() {
        setImageDrawable(record)
        imageTintList = redTint
        backgroundTintList = secondaryContainerTint
        state = State.RECORD
    }

    fun playToPause() {
        if (state == State.PAUSE) return
        if (!isLaidOut) {
            forcePause()
            return
        }
        setImageDrawable(playToPause?.apply { start() })
        imageTintList = tertiaryTint
        backgroundTintList = tertiaryContainerTint
        state = State.PAUSE
    }

    fun pauseToPlay() {
        if (state == State.PLAY) return
        if (!isLaidOut) {
            forcePlay()
            return
        }
        setImageDrawable(pauseToPlay?.apply { start() })
        imageTintList = tertiaryTint
        backgroundTintList = tertiaryContainerTint
        state = State.PLAY
    }

    private enum class State {
        PLAY, PAUSE, MIC, RECORD, FORCE_PLAY, FORCE_PAUSE
    }
}