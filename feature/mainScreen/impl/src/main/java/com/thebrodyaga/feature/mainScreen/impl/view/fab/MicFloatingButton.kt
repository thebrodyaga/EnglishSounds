package com.thebrodyaga.feature.mainScreen.impl.view.fab

import android.Manifest
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.View.OnClickListener
import android.view.View.OnLongClickListener
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.thebrodyaga.core.uiUtils.view.viewScope
import com.thebrodyaga.feature.audioPlayer.api.RecordState
import com.thebrodyaga.feature.audioPlayer.api.RecordVoice
import com.thebrodyaga.feature.mainScreen.impl.R
import dev.shreyaspatil.permissionFlow.PermissionFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class MicFloatingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FloatingActionButton(context, attrs) {

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
    private val permissionFlow = PermissionFlow.getInstance()

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
        recordVoice.state
            .onEach { state: RecordState ->
                setViewMode(state)
            }.launchIn(viewScope)
    }

    private fun setViewMode(state: RecordState) {
        mode = when (state) {
            RecordState.EMPTY -> Mode.MIC
            RecordState.RECORDING -> Mode.RECORDING
            RecordState.AUDIO -> Mode.PLAY
            RecordState.PLAYING_AUDIO -> Mode.PLAYING
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

    fun observePermission() {
        permissionFlow.getPermissionState(Manifest.permission.RECORD_AUDIO)
            .onEach { state ->
                if (state.isGranted) {
                    recordVoice?.startRecord()
                } else {
                    Toast.makeText(context, R.string.need_record_permission, Toast.LENGTH_SHORT)
                        .show()
                }
            }
            .launchIn(viewScope)
    }

    private val onStartRecordClick = OnClickListener {
        observePermission()
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