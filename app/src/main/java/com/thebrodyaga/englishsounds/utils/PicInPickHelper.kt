package com.thebrodyaga.englishsounds.utils

import android.app.Activity
import android.app.PendingIntent
import android.app.PictureInPictureParams
import android.app.RemoteAction
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Icon
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.IntDef
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import com.thebrodyaga.englishsounds.R

@RequiresApi(Build.VERSION_CODES.O)
class PicInPickHelper constructor(private val activity: Activity) {

    private val picInPicParamsBuilder: PictureInPictureParams.Builder =
        PictureInPictureParams.Builder()

    @RequiresApi(Build.VERSION_CODES.O)
    fun actionsEmpty(): PictureInPictureParams.Builder = picInPicParamsBuilder.setActions(null)

    @RequiresApi(Build.VERSION_CODES.O)
    fun actionsForRunning(
        isPlaying: Boolean,
        setSeekTo: Boolean = false
    ): PictureInPictureParams.Builder {
        val actions: ArrayList<RemoteAction> = ArrayList()
        val maxActions = activity.maxNumPictureInPictureActions
        when {
            maxActions == 1 -> addPlayOrPauseActions(actions, isPlaying)
            maxActions >= 3 -> {
                if (setSeekTo)
                    addSeekToActions(actions, false)
                addPlayOrPauseActions(actions, isPlaying)
                if (setSeekTo)
                    addSeekToActions(actions, true)
            }
        }
        return picInPicParamsBuilder.setActions(actions)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun actionsForEnd(): PictureInPictureParams.Builder {
        val actions: ArrayList<RemoteAction> = ArrayList()
        val maxActions = activity.maxNumPictureInPictureActions
        when {
            maxActions >= 1 -> actions.add(
                newRemoteAction(
                    R.drawable.ic_replay, R.string.exo_controls_repeat_one_description,
                    PIP_CONTROL_TYPE_REPEAT,
                    PIP_REQUEST_REPEAT
                )
            )
        }
        return picInPicParamsBuilder.setActions(actions)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addSeekToActions(actions: ArrayList<RemoteAction>, isFastForward: Boolean) {
        actions.add(
            if (isFastForward) newRemoteAction(
                R.drawable.ic_fast_forward, R.string.exo_controls_fastforward_description,
                PIP_CONTROL_TYPE_FAST_FORWARD,
                PIP_REQUEST_FAST_FORWARD
            )
            else newRemoteAction(
                R.drawable.ic_fast_rewind, R.string.exo_controls_rewind_description,
                PIP_CONTROL_TYPE_REWIND,
                PIP_REQUEST_REWIND
            )
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addPlayOrPauseActions(actions: ArrayList<RemoteAction>, isPlaying: Boolean) {
        actions.add(
            if (isPlaying) newRemoteAction(
                R.drawable.ic_pause, R.string.exo_controls_pause_description,
                PIP_CONTROL_TYPE_PAUSE,
                PIP_REQUEST_PAUSE
            )
            else newRemoteAction(
                R.drawable.ic_play, R.string.exo_controls_play_description,
                PIP_CONTROL_TYPE_PLAY,
                PIP_REQUEST_PLAY
            )
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun newRemoteAction(
        @DrawableRes iconId: Int, @StringRes title: Int,
        controlType: Int, requestCode: Int
    ): RemoteAction {
        val intent = PendingIntent.getBroadcast(
            activity,
            requestCode,
            Intent(ACTION_MEDIA_CONTROL)
                .putExtra(EXTRA_CONTROL_TYPE, controlType),
            0
        )
        val icon: Icon = Icon.createWithResource(activity, iconId)
        val titleStr = activity.getString(title)
        return RemoteAction(icon, titleStr, titleStr, intent)
    }

    fun enterPicInPic(activity: Activity) {
        if (!activity.isHavePicInPicMode())
            return
        activity.enterPictureInPictureMode(
            with(picInPicParamsBuilder) {
                val width = 16
                val height = 9
                setAspectRatio(android.util.Rational(width, height))
                build()
            })
    }

    companion object {

        fun Activity.isHavePicInPicMode() =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
                    packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)
    }
}

class PicInPicReceiver constructor(
    private val onReceiveControl: (controlType: @PicInPicControlType Int) -> Unit
) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null || ACTION_MEDIA_CONTROL != intent.action)
            return

        // This is where we are called back from Picture-in-Picture action items.
        onReceiveControl(intent.getIntExtra(EXTRA_CONTROL_TYPE, 0))
    }
}

/** Intent action for media controls from Picture-in-Picture mode.  */
const val ACTION_MEDIA_CONTROL = "media_control"

/** Intent extra for media controls from Picture-in-Picture mode.  */
const val EXTRA_CONTROL_TYPE = "control_type"

@Target(AnnotationTarget.TYPE)
@Retention(AnnotationRetention.SOURCE)
@IntDef(
    PIP_REQUEST_PLAY,
    PIP_REQUEST_PAUSE,
    PIP_REQUEST_FAST_FORWARD,
    PIP_REQUEST_REWIND,
    PIP_REQUEST_REPEAT
)
annotation class PicInPicRequestType

/** The request code for actions in PendingIntent.  */
const val PIP_REQUEST_PLAY = 1
const val PIP_REQUEST_PAUSE = 2
const val PIP_REQUEST_FAST_FORWARD = 3
const val PIP_REQUEST_REWIND = 4
const val PIP_REQUEST_REPEAT = 5

@Target(AnnotationTarget.TYPE)
@Retention(AnnotationRetention.SOURCE)
@IntDef(
    PIP_CONTROL_TYPE_PLAY,
    PIP_CONTROL_TYPE_PAUSE,
    PIP_CONTROL_TYPE_FAST_FORWARD,
    PIP_CONTROL_TYPE_REWIND,
    PIP_CONTROL_TYPE_REPEAT
)
annotation class PicInPicControlType

/** The intent extra value for actions.  */
const val PIP_CONTROL_TYPE_PLAY = 1
const val PIP_CONTROL_TYPE_PAUSE = 2
const val PIP_CONTROL_TYPE_FAST_FORWARD = 3
const val PIP_CONTROL_TYPE_REWIND = 4
const val PIP_CONTROL_TYPE_REPEAT = 5