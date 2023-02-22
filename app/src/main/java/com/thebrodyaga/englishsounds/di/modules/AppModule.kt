package com.thebrodyaga.englishsounds.di.modules

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.thebrodyaga.englishsounds.domine.interactors.AllVideoInteractor
import com.thebrodyaga.data.sounds.api.SoundsRepository
import com.thebrodyaga.data.sounds.api.SoundsVideoRepository
import com.thebrodyaga.feature.mainScreen.api.MainScreenFactory
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    @Singleton
    fun provideContext(application: Application): Context = application.applicationContext

    @Provides
    fun mainScreenFactory(): MainScreenFactory = TODO()

    @Provides
    @Singleton
    fun provideAllVideoInteractor(
        soundsRepository: SoundsRepository,
        soundsVideoRepository: SoundsVideoRepository
    ) = AllVideoInteractor(soundsRepository, soundsVideoRepository)

    @Provides
    @Singleton
    fun provideGson(): Gson =
        GsonBuilder().create()

    @Provides
    @Singleton
    fun provideAppSharedPreferences(context: Context): SharedPreferences =
        context.getSharedPreferences("AppSharedPreferences", Context.MODE_PRIVATE)
}