package com.thebrodyaga.englishsounds.activity.di.feature

import com.thebrodyaga.englishsounds.base.di.ActivityScope
import com.thebrodyaga.feature.audioPlayer.api.AudioPlayer
import com.thebrodyaga.feature.audioPlayer.api.RecordVoice
import com.thebrodyaga.feature.audioPlayer.impl.AudioPlayerImpl
import com.thebrodyaga.feature.audioPlayer.impl.RecordVoiceImpl
import dagger.Binds
import dagger.Module

@Module
interface AudioPlayerFeatureModule {
    @Binds
    @ActivityScope
    fun audioPlayer(audioPlayerImpl: AudioPlayerImpl): AudioPlayer

    @Binds
    @ActivityScope
    fun recordVoice(recordVoiceImpl: RecordVoiceImpl): RecordVoice
}