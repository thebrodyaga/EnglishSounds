package com.thebrodyaga.feature.soundDetails.impl.ui

import com.thebrodyaga.data.sounds.api.model.AmericanSoundDto
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType

interface SoundView:MvpView {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setData(
        list: List<Any>,
        soundDto: AmericanSoundDto
    )
}