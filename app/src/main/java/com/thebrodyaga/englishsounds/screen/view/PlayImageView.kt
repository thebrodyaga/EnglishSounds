package com.thebrodyaga.englishsounds.screen.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.Player
import com.thebrodyaga.englishsounds.R
import com.thebrodyaga.feature.audioPlayer.impl.AudioPlayer
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import java.io.File

class PlayImageView : AppCompatImageView, Player.EventListener {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr)

    private var audioPlayer: AudioPlayer? = null
    var audioFile: File? = null

    private val compositeDisposable = CompositeDisposable()
    private val playIcon =
        ContextCompat.getDrawable(context, R.drawable.ic_play) ?: throw IllegalArgumentException()
    private val stopPlayIcon =
        ContextCompat.getDrawable(context, R.drawable.ic_stop) ?: throw IllegalArgumentException()
    private val onPlayClick =
        OnClickListener { audioPlayer?.playAudio(audioFile ?: return@OnClickListener, this) }
    private val onStopPlayClick = OnClickListener { audioPlayer?.stopPlay() }

    private var mode: Mode = Mode.PLAY
        set(value) {
            when (value) {
                Mode.PLAY -> bindAudioState()
                Mode.PLAYING -> bindPlayingAudioState()
            }
            field = value
        }

    init {
        mode = Mode.PLAY
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        Timber.i("${audioFile?.path} isPlaying =  $isPlaying")
        mode = if (isPlaying) Mode.PLAYING else Mode.PLAY
    }

    private fun bindPlayingAudioState() {
        setImageDrawable(stopPlayIcon)
        setOnClickListener(onStopPlayClick)
    }

    private fun bindAudioState() {
        setImageDrawable(playIcon)
        setOnClickListener(onPlayClick)
    }

    fun setImage(icon: Drawable) {
        Glide.with(context)
            .load(icon)
            .into(this)
    }

    fun setRecordVoice(audioPlayer: AudioPlayer) {
        this.audioPlayer = audioPlayer
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        compositeDisposable.clear()
    }

    private enum class Mode {
        PLAY, PLAYING
    }
}