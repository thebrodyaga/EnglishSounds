package com.thebrodyaga.brandbook.component.mic

import android.content.Context
import android.util.AttributeSet
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.thebrodyaga.brandbook.R
import com.thebrodyaga.core.uiUtils.common.getColorStateList

class MicFloatingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FloatingActionButton(context, attrs) {

    private val pauseToPlay = AnimatedVectorDrawableCompat.create(context, R.drawable.ic_pause_to_play)
    private val playToPause = AnimatedVectorDrawableCompat.create(context, R.drawable.ic_play_to_pause)
    private var state: State = State.PLAY

    init {
        playToPause?.setTintList(getColorStateList(R.attr.colorPrimary))
        pauseToPlay?.setTintList(getColorStateList(R.attr.colorPrimary))
        setImageDrawable(pauseToPlay)
        setImageDrawable(playToPause)
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
}