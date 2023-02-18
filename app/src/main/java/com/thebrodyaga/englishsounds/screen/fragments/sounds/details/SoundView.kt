package com.thebrodyaga.englishsounds.screen.fragments.sounds.details

import com.thebrodyaga.data.sounds.api.model.AmericanSoundDto
import com.thebrodyaga.englishsounds.domine.entities.ui.SoundsDetailsListItem
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