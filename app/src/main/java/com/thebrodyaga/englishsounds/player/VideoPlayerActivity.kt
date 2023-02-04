package com.thebrodyaga.englishsounds.player

import android.app.PictureInPictureParams
import android.content.res.Configuration
import android.media.AudioManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Rational
import com.thebrodyaga.englishsounds.R
import kotlinx.android.synthetic.main.activity_video_player.*

class VideoPlayerActivity : AppCompatActivity() {

    private lateinit var playerHolder: PlayerHolder
    private lateinit var mediaManager: MediaManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)
        // While the user is in the app, the volume controls should adjust the music volume.
        volumeControlStream = AudioManager.STREAM_MUSIC
        playerHolder = PlayerHolder(this, this)
        mediaManager = MediaManager(this, playerHolder, this)
        exo_player_view.player = playerHolder.exoPlayer

        addOnPictureInPictureModeChangedListener {
            exo_player_view.useController = !it.isInPictureInPictureMode
        }
    }

    // Picture in Picture related functions.
    override fun onUserLeaveHint() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            enterPictureInPictureMode(
                with(PictureInPictureParams.Builder()) {
                    val width = 16
                    val height = 9
                    setAspectRatio(Rational(width, height))
                    build()
                })
        }
    }
}
