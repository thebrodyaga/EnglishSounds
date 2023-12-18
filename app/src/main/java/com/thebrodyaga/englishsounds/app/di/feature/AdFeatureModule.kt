package com.thebrodyaga.englishsounds.app.di.feature

import com.thebrodyaga.ad.api.AppAdLoader
import com.thebrodyaga.ad.google.GoogleAdLoader
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface AdFeatureModule {
    @Binds
    @Singleton
    fun appAdLoader(appAdLoader: GoogleAdLoader): AppAdLoader
}