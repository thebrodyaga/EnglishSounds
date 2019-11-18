package com.thebrodyaga.englishsounds.screen.fragments.sounds.training

import com.thebrodyaga.englishsounds.domine.entities.data.PracticeWordDto
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType

interface SoundsTrainingView : MvpView {
    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setData(list: List<PracticeWordDto>)
}