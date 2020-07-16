package com.thebrodyaga.englishsounds.screen.fragments.video.listoflists

import com.thebrodyaga.englishsounds.domine.entities.ui.SoundsListItem
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType

interface ListOfVideoListsView:MvpView {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setListData(videos: List<SoundsListItem>)
}