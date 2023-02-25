package com.thebrodyaga.legacy.delegates

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.thebrodyaga.legacy.ShortAdItem
import com.thebrodyaga.legacy.databinding.ItemAdVerticalShortBinding
import com.thebrodyaga.legacy.utils.CompositeAdLoader

fun videoNativeShortAdDelegate(
    nativeAdLoader: CompositeAdLoader
) = adapterDelegateViewBinding<ShortAdItem, Any, ItemAdVerticalShortBinding>(
    { layoutInflater, root -> ItemAdVerticalShortBinding.inflate(layoutInflater, root, false) }
) {

    bind {
        binding.adRootView.setAd(item, nativeAdLoader, adapterPosition)
    }

    onViewRecycled { binding.adRootView.dispose() }
}