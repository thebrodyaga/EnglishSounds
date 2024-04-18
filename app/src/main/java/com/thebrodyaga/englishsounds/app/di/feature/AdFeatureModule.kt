package com.thebrodyaga.englishsounds.app.di.feature

import android.app.Application
import com.thebrodyaga.ad.api.AppAdManager
import com.thebrodyaga.ad.api.SingleAdLoader
import com.thebrodyaga.ad.google.GoogleAdManager
import com.thebrodyaga.ad.google.GoogleMobileAdsConsentManager
import com.thebrodyaga.ad.google.GoogleSingleAdLoader
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
interface AdFeatureModule {
    @Binds
    @Singleton
    fun appAdLoader(
        appAdLoader: GoogleAdManager,
    ): AppAdManager

    @Binds
    fun singleAdLoader(
        singleAdLoader: GoogleSingleAdLoader,
    ): SingleAdLoader

    companion object {
        @Provides
        @Singleton
        fun googleMobileAdsConsentManager(app: Application): GoogleMobileAdsConsentManager {
            return GoogleMobileAdsConsentManager(app)
        }
    }
}