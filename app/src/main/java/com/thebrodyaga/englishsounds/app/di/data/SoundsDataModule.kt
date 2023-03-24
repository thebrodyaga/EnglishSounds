package com.thebrodyaga.englishsounds.app.di.data

import com.thebrodyaga.data.sounds.api.SoundsRepository
import com.thebrodyaga.data.sounds.api.SoundsVideoRepository
import com.thebrodyaga.data.sounds.impl.AmericanSoundsRepositoryImpl
import com.thebrodyaga.data.sounds.impl.SoundsVideoRepositoryImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface SoundsDataModule {
    @Binds
    @Singleton
    fun soundsRepository(americanSoundsRepositoryImpl: AmericanSoundsRepositoryImpl): SoundsRepository

    @Binds
    @Singleton
    fun soundsVideoRepository(soundsVideoRepositoryImpl: SoundsVideoRepositoryImpl): SoundsVideoRepository
}