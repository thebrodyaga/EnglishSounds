package com.thebrodyaga.feature.training.impl

import com.thebrodyaga.data.sounds.api.model.PracticeWordDto
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType

interface SoundsTrainingView : MvpView {
    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setData(list: List<PracticeWordDto>)
}