package com.thebrodyaga.englishsounds.di.modules

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.thebrodyaga.englishsounds.domine.interactors.AllVideoInteractor
import com.thebrodyaga.data.sounds.api.SoundsRepository
import com.thebrodyaga.data.sounds.api.SoundsVideoRepository
import com.thebrodyaga.data.sounds.impl.AmericanSoundsRepositoryImpl
import com.thebrodyaga.data.sounds.impl.SoundsVideoRepositoryImpl
import com.thebrodyaga.feature.audioPlayer.api.AudioPlayer
import com.thebrodyaga.feature.audioPlayer.api.RecordVoice
import com.thebrodyaga.data.sounds.api.SettingManager
import com.thebrodyaga.data.sounds.impl.setting.SettingManagerImpl
import com.thebrodyaga.feature.audioPlayer.impl.AudioPlayerImpl
import com.thebrodyaga.feature.audioPlayer.impl.RecordVoiceImpl
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
    fun provideRecordVoice(
        audioPlayer: AudioPlayer,
        context: Context
    ): RecordVoice = RecordVoiceImpl(audioPlayer, context)

    @Provides
    @Singleton
    fun provideAudioPlayer(context: Context): AudioPlayer = AudioPlayerImpl(context)

    @Provides
    @Singleton
    fun provideAllVideoInteractor(
        soundsRepository: SoundsRepository,
        soundsVideoRepository: SoundsVideoRepository
    ) = AllVideoInteractor(soundsRepository, soundsVideoRepository)

    @Provides
    @Singleton
    fun provideSoundRepository(
        context: Context,
        gson: Gson,
        settingManager: SettingManager
    ): SoundsRepository =
        AmericanSoundsRepositoryImpl(context, gson, settingManager)

    @Provides
    @Singleton
    fun provideSoundVideoRepository(
        context: Context
    ): SoundsVideoRepository =
        SoundsVideoRepositoryImpl(context)

    @Provides
    @Singleton
    fun provideGson(): Gson =
        GsonBuilder().create()

    @Provides
    @Singleton
    fun provideAppSharedPreferences(context: Context): SharedPreferences =
        context.getSharedPreferences("AppSharedPreferences", Context.MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideSetting(sharedPreferences: SharedPreferences, gson: Gson): SettingManager =
        SettingManagerImpl(sharedPreferences, gson)

}