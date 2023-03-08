package com.thebrodyaga.englishsounds.di.feature

import com.thebrodyaga.feature.audioPlayer.api.AudioPlayer
import com.thebrodyaga.feature.audioPlayer.api.RecordVoice
import com.thebrodyaga.feature.audioPlayer.impl.AudioPlayerImpl
import com.thebrodyaga.feature.audioPlayer.impl.RecordVoiceImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface AudioPlayerFeatureModule {
    @Binds
    @Singleton
    fun audioPlayer(audioPlayerImpl: AudioPlayerImpl): AudioPlayer

    @Binds
    @Singleton
    fun recordVoice(recordVoiceImpl: RecordVoiceImpl): RecordVoice
}