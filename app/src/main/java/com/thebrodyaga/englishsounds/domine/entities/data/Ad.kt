package com.thebrodyaga.englishsounds.domine.entities.data

import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.thebrodyaga.englishsounds.domine.entities.ui.SoundsListItem

data class AdListBox constructor(
    val count: Int,
    val ads: List<AdBox>
)

data class AdBox constructor(
    val ad: UnifiedNativeAd? = null
): SoundsListItem