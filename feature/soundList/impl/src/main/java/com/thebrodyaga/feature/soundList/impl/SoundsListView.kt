package com.thebrodyaga.feature.soundList.impl

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType

interface SoundsListView : MvpView {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setListData(sounds: List<Any>)
}