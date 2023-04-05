package com.thebrodyaga.brandbook.component.play

import android.content.Context
import android.util.AttributeSet
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.google.android.material.button.MaterialButton
import com.thebrodyaga.brandbook.R
import com.thebrodyaga.core.uiUtils.common.getColorStateList

class PlayButtonView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : MaterialButton(context, attrs, R.attr.materialIconButtonStyle) {

    private val pauseToPlay = AnimatedVectorDrawableCompat.create(context, R.drawable.ic_pause_to_play)
    private val playToPause = AnimatedVectorDrawableCompat.create(context, R.drawable.ic_play_to_pause)
    private var state: State = State.PLAY

    init {
        icon = (pauseToPlay)
        icon = (playToPause)
    }

    fun bind(model: PlayButtonUiModel) {
        iconTint = getColorStateList(model.iconTint)
        when (val state = model.state) {
            is PlayButtonBindingState.PauseToPlay -> if (state.force) forcePlay() else pauseToPlay()
            is PlayButtonBindingState.PlayToPause -> if (state.force) forcePause() else playToPause()
        }
    }

    fun togglePlaying() {
        when (state) {
            State.PLAY -> playToPause()
            State.PAUSE -> pauseToPlay()
        }
    }

    fun forcePause() {
        icon = (pauseToPlay)
        state = State.PAUSE
    }

    fun forcePlay() {
        icon = (playToPause)
        state = State.PLAY
    }

    fun playToPause() {
        if (state == State.PAUSE) return
        if (!isLaidOut) {
            forcePause()
            return
        }
        icon = (playToPause?.apply { start() })
        state = State.PAUSE
    }

    fun pauseToPlay() {
        if (state == State.PLAY) return
        if (!isLaidOut) {
            forcePlay()
            return
        }
        icon = (pauseToPlay?.apply { start() })
        state = State.PLAY
    }

    private enum class State {
        PLAY, PAUSE
    }
}