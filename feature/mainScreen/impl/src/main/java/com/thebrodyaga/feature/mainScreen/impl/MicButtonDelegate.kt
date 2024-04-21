package com.thebrodyaga.feature.mainScreen.impl

import android.app.Activity
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.thebrodyaga.brandbook.component.mic.MicFloatingButton
import com.thebrodyaga.core.uiUtils.launchWithLifecycle
import com.thebrodyaga.feature.audioPlayer.api.RecordState
import com.thebrodyaga.feature.audioPlayer.api.RecordVoice
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class MicButtonDelegate {

    fun bind(
        fragment: Fragment,
        fab: MicFloatingButton,
        recordVoice: RecordVoice,
        permissionLauncher: ActivityResultLauncher<Array<String>>
    ) {
        val vibrator = ContextCompat.getSystemService(fab.context, Vibrator::class.java)
        val longClick = View.OnLongClickListener {
            vibrator?.cancel()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val vibrationEffect = VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK)
                vibrator?.vibrate(vibrationEffect)
            } else {
                vibrator?.vibrate(300)
            }
            recordVoice.clearRecord()
            true
        }
        fab.setOnClickListener {
            val permission = android.Manifest.permission.RECORD_AUDIO
            val isGranted = ContextCompat.checkSelfPermission(fab.context, permission) == PERMISSION_GRANTED
            val shouldShowDialog = (fragment.activity as? Activity)?.let {
                ActivityCompat.shouldShowRequestPermissionRationale(it, permission)
            } ?: false
            when {
                shouldShowDialog -> {
                    // todo
                }
                isGranted -> when (recordVoice.state.value) {
                    RecordState.ReadyToRecord -> {
                        fab.record()
                        recordVoice.startRecord()
                    }
                    RecordState.Recording -> {
                        fab.forcePlay()
                        recordVoice.stopRecord()
                    }
                    is RecordState.Audio -> {
                        recordVoice.playRecord()
                    }
                    is RecordState.PlayingAudio -> {
                        recordVoice.stopPlayRecord()
                    }
                }
                else -> permissionLauncher.launch(arrayOf(permission))
            }
        }

        recordVoice.state
            .onEach { state ->
                when (state) {
                    RecordState.ReadyToRecord -> {
                        fab.setOnLongClickListener(null)
                        fab.mic()
                    }
                    RecordState.Recording -> {
                        fab.setOnLongClickListener(null)
                        fab.record()
                    }
                    is RecordState.Audio -> {
                        fab.setOnLongClickListener(longClick)
                        if (state.isWhenPlayingChanged) fab.pauseToPlay() else fab.forcePlay()
                    }
                    is RecordState.PlayingAudio -> {
                        fab.setOnLongClickListener(longClick)
                        if (state.isPlayingChanged) fab.playToPause() else fab.forcePause()

                        fab.playToPause()
                    }
                }
            }
            .launchWithLifecycle(fragment.viewLifecycleOwner.lifecycle)
    }
}