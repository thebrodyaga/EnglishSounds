package com.thebrodyaga.ad.api

import com.google.android.gms.ads.nativead.NativeAd
import com.thebrodyaga.brandbook.model.UiModel


fun AppAd.google(): AppAd.Google? = (this as? AppAd.Google)

sealed interface AppAd {
    object Empty : AppAd
    object Loading : AppAd
    data class Google(
        val loadedTime: Long,
        val ad: NativeAd,
    ) : AppAd
}

data class GoogleAdUiModel(
    val ad: NativeAd,
    val isWithMedia: Boolean,
) : UiModel

object AdLoadingBigUiModel : UiModel
object AdLoadingSmallUiModel : UiModel