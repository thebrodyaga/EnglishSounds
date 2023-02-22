package com.thebrodyaga.englishsounds.screen.fragments.video.listoflists

import com.thebrodyaga.legacy.SoundsListItem
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType

interface ListOfVideoListsView:MvpView {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setListData(videos: List<SoundsListItem>)
}