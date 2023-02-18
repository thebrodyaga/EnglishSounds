package com.thebrodyaga.englishsounds.screen.view.fab

import android.Manifest
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.View.OnClickListener
import android.view.View.OnLongClickListener
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.tbruyelle.rxpermissions2.RxPermissions
import com.thebrodyaga.englishsounds.R
import com.thebrodyaga.feature.audioPlayer.impl.RecordVoice
import io.reactivex.disposables.CompositeDisposable


class MicFloatingButton : FloatingActionButton {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr)

    private var mode: Mode =
        Mode.MIC
        set(value) {
            when (value) {
                Mode.MIC -> bindEmptyState()
                Mode.RECORDING -> bindRecordState()
                Mode.PLAY -> bindAudioState()
                Mode.PLAYING -> bindPlayingAudioState()
            }
            field = value
        }
    private val compositeDisposable = CompositeDisposable()
    private val rxPermissions = RxPermissions(this.let { it.context as FragmentActivity })

    private val micIcon =
        ContextCompat.getDrawable(context, R.drawable.ic_mic) ?: throw IllegalArgumentException()
    private val recordIcon =
        ContextCompat.getDrawable(context, R.drawable.ic_record) ?: throw IllegalArgumentException()
    private val playIcon =
        ContextCompat.getDrawable(context, R.drawable.ic_play) ?: throw IllegalArgumentException()
    private val stopPlayIcon =
        ContextCompat.getDrawable(context, R.drawable.ic_stop) ?: throw IllegalArgumentException()

    private val accentColor = ContextCompat.getColor(context, R.color.colorPrimary)
    private val accentColorState = ContextCompat.getColorStateList(context, R.color.colorPrimary)
    private val redColor = ContextCompat.getColor(context, R.color.error_object)
    private val redColorState = ContextCompat.getColorStateList(context, R.color.error_object)

    private var recordVoice: RecordVoice? = null

    override fun hide() {
        super.hide(object : FloatingActionButton.OnVisibilityChangedListener() {
            override fun onHidden(fab: FloatingActionButton) {
                super.onHidden(fab)
                fab.visibility = View.INVISIBLE
            }
        })
    }

    fun setRecordVoice(recordVoice: RecordVoice) {
        this.recordVoice = recordVoice
        subscribeOnRecordFileChange(recordVoice)
    }

    private fun subscribeOnRecordFileChange(recordVoice: RecordVoice) {
        compositeDisposable.add(recordVoice.stateSubject
            .distinctUntilChanged()
            .subscribe { state: RecordVoice.RecordState ->
                setViewMode(state)
            })
    }

    private fun setViewMode(state: RecordVoice.RecordState) {
        mode = when (state) {
            RecordVoice.RecordState.EMPTY -> Mode.MIC
            RecordVoice.RecordState.RECORDING -> Mode.RECORDING
            RecordVoice.RecordState.AUDIO -> Mode.PLAY
            RecordVoice.RecordState.PLAYING_AUDIO -> Mode.PLAYING
        }
    }

    private fun bindPlayingAudioState() {
        setImageDrawable(stopPlayIcon)
        backgroundTintList = accentColorState
        setColorFilter(accentColor, android.graphics.PorterDuff.Mode.SRC_IN)
        setOnClickListener(onStopPlayRecordClick)
        setOnLongClickListener(onDeleteRecordClick)
    }

    private fun bindAudioState() {
        setImageDrawable(playIcon)
        backgroundTintList = accentColorState
        setColorFilter(accentColor, android.graphics.PorterDuff.Mode.SRC_IN)
        setOnClickListener(onPlayRecordClick)
        setOnLongClickListener(onDeleteRecordClick)

    }

    private fun bindRecordState() {
        backgroundTintList = redColorState
        setColorFilter(redColor, android.graphics.PorterDuff.Mode.SRC_IN)
        setImageDrawable(recordIcon)
        setOnClickListener(onStopRecordClick)
        setOnLongClickListener(null)

    }

    private fun bindEmptyState() {
        setImageDrawable(micIcon)
        backgroundTintList = accentColorState
        setColorFilter(accentColor, android.graphics.PorterDuff.Mode.SRC_IN)
        setOnClickListener(onStartRecordClick)
        setOnLongClickListener(null)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        compositeDisposable.clear()
    }

    private val onStartRecordClick = OnClickListener {
        rxPermissions
            .request(Manifest.permission.RECORD_AUDIO)
            .subscribe { granted ->
                if (granted) {
                    recordVoice?.startRecord()
                } else {
                    Toast.makeText(context, R.string.need_record_permission, Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }
    private val onStopRecordClick = OnClickListener { recordVoice?.stopRecord() }
    private val onPlayRecordClick = OnClickListener { recordVoice?.playRecord() }
    private val onStopPlayRecordClick = OnClickListener { recordVoice?.stopPlayRecord() }
    private val onDeleteRecordClick = OnLongClickListener {
        recordVoice?.clearRecord()
        true
    }

    private enum class Mode {
        MIC, RECORDING, PLAY, PLAYING
    }

}