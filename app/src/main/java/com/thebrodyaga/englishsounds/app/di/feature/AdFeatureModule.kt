package com.thebrodyaga.englishsounds.app.di.feature

import android.app.Application
import com.thebrodyaga.ad.api.AppAdLoader
import com.thebrodyaga.ad.google.GoogleAdLoader
import com.thebrodyaga.ad.google.GoogleMobileAdsConsentManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
interface AdFeatureModule {
    @Binds
    @Singleton
    fun appAdLoader(
        appAdLoader: GoogleAdLoader,
    ): AppAdLoader

    companion object {
        @Provides
        @Singleton
        fun googleMobileAdsConsentManager(app: Application): GoogleMobileAdsConsentManager {
            return GoogleMobileAdsConsentManager(app)
        }
    }
}