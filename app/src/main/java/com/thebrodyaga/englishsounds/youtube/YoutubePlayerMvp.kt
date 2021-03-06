package com.thebrodyaga.englishsounds.youtube

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.thebrodyaga.englishsounds.screen.base.BasePresenter
import moxy.InjectViewState
import moxy.MvpView
import javax.inject.Inject

@InjectViewState
class YoutubePlayerPresenter @Inject constructor() : BasePresenter<MvpView>() {

    var currentSecond = 0f
    var playerState: PlayerConstants.PlayerState = PlayerConstants.PlayerState.UNKNOWN
}