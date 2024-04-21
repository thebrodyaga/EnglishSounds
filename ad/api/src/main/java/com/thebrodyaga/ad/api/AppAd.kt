package com.thebrodyaga.ad.api

import com.google.android.gms.ads.nativead.NativeAd
import com.thebrodyaga.brandbook.model.UiModel


fun AppAd.google(): AppAd.Google? = (this as? AppAd.Google)

sealed interface AppAd {
    data object Empty : AppAd
    data object Loading : AppAd
    data class Google(
        val adId: String,
    ) : AppAd {
        private var _loadedTime: Long? = null
        private var _ad: NativeAd? = null
        val loadedTime: Long
            get() = _loadedTime!!
        val ad: NativeAd
            get() = _ad!!

        constructor(
            adId: String,
            loadedTime: Long,
            ad: NativeAd
        ) : this(adId) {
            _loadedTime = loadedTime
            _ad = ad
        }
    }
}

data class GoogleAdUiModel(
    val ad: NativeAd,
    val isWithMedia: Boolean,
) : UiModel

object AdLoadingBigUiModel : UiModel
object AdLoadingSmallUiModel : UiModel

enum class AdType {
    SOUND_LIST,
    SOUND_DETAILS,
    TRAINING,
    VIDEO_LIST,
}