package com.thebrodyaga.feature.youtube.impl

import androidx.lifecycle.ViewModel
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import javax.inject.Inject

class YoutubePlayerViewModel @Inject constructor() : ViewModel() {

    var currentSecond = 0f
    var playerState: PlayerConstants.PlayerState = PlayerConstants.PlayerState.UNKNOWN
}