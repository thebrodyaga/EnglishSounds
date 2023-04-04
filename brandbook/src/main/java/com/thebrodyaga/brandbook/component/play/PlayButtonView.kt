package com.thebrodyaga.brandbook.component.play

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.thebrodyaga.brandbook.R
import com.thebrodyaga.brandbook.model.UiModel
import com.thebrodyaga.core.uiUtils.common.getColorStateList
import com.thebrodyaga.core.uiUtils.ripple.rippleForeground
import com.thebrodyaga.core.uiUtils.shape.shapeCircle

class PlayButtonView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatImageView(context, attrs) {

    private val pauseToPlay = AnimatedVectorDrawableCompat.create(context, R.drawable.ic_pause_to_play)
    private val playToPause = AnimatedVectorDrawableCompat.create(context, R.drawable.ic_play_to_pause)
    private var state: State = State.PLAY

    init {
        setImageDrawable(pauseToPlay)
        setImageDrawable(playToPause)
        rippleForeground(shapeCircle())
    }

    fun bind(model: PlayButtonUiModel) {
        val iconTint = model.iconTint
        iconTint?.let { this.imageTintList = getColorStateList(iconTint) }
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

data class PlayButtonUiModel(
    val state: PlayButtonBindingState,
    val iconTint: Int? = null,
) : UiModel

sealed interface PlayButtonBindingState {
    val force: Boolean

    data class PlayToPause(
        override val force: Boolean = false
    ) : PlayButtonBindingState

    data class PauseToPlay(
        override val force: Boolean = false
    ) : PlayButtonBindingState
}