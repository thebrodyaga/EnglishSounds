package com.thebrodyaga.englishsounds.screen.fragments.sounds.list

import com.thebrodyaga.englishsounds.domine.entities.ui.SoundsListItem
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType

interface SoundsListView : MvpView {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setListData(sounds: List<SoundsListItem>)
}