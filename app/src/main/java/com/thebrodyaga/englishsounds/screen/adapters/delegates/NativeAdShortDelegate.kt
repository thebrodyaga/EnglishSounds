package com.thebrodyaga.englishsounds.screen.adapters.delegates

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import com.thebrodyaga.englishsounds.R
import com.thebrodyaga.englishsounds.domine.entities.ui.ShortAdItem
import com.thebrodyaga.englishsounds.utils.CompositeAdLoader
import kotlinx.android.synthetic.main.item_ad_vertical_short.*

fun videoNativeShortAdDelegate(
    nativeAdLoader: CompositeAdLoader
) = adapterDelegateLayoutContainer<ShortAdItem, Any>(
    R.layout.item_ad_vertical_short
) {

    bind {
        ad_root_view.setAd(item, nativeAdLoader, adapterPosition)
    }

    onViewRecycled { ad_root_view.dispose() }
}