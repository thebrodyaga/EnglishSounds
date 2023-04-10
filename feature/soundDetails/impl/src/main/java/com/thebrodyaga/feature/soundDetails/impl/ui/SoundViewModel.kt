package com.thebrodyaga.feature.soundDetails.impl.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thebrodyaga.base.navigation.api.RouterProvider
import com.thebrodyaga.brandbook.component.data.DataUiModel
import com.thebrodyaga.brandbook.model.UiModel
import com.thebrodyaga.data.sounds.api.SoundsRepository
import com.thebrodyaga.data.sounds.api.model.AmericanSoundDto
import com.thebrodyaga.feature.audioPlayer.api.AudioPlayer
import com.thebrodyaga.feature.audioPlayer.api.AudioPlayerState
import com.thebrodyaga.feature.soundDetails.impl.ui.mapper.SoundDetailsMapper
import com.thebrodyaga.feature.youtube.api.PlayVideoExtra
import com.thebrodyaga.feature.youtube.api.YoutubeScreenFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class SoundViewModel @Inject constructor(
    private val repository: SoundsRepository,
    private val mapper: SoundDetailsMapper,
    private val transcription: String,
    private val audioPlayer: AudioPlayer,
    private val youtubeScreenFactory: YoutubeScreenFactory,
    private val routerProvider: RouterProvider,
) : ViewModel() {

    private val state = MutableStateFlow<SoundState>(SoundState.Empty)
    fun getState() = state.asStateFlow()

    init {
        repository.getSounds(transcription).combine(audioPlayer.state())
        { sounds, player -> sounds to player }
            .flowOn(Dispatchers.IO)
            .onEach { mapForUi(it) }
            .onCompletion { it?.let { Timber.e(it) } }
            .launchIn(viewModelScope)
    }

    private fun mapForUi(pair: Pair<AmericanSoundDto, AudioPlayerState>) {
        state.value = SoundState.Content(mapper.mapFullList(pair.first, pair.second), pair.first)
    }

    fun onAudioItemClick(item: DataUiModel) {
        val audio = item.payload as? File ?: return
        audioPlayer.playAudio(audio)
    }

    fun onVideoItemClick(videoUrl: String) {
        routerProvider.anyRouter.navigateTo(
            youtubeScreenFactory.youtubeScreen(PlayVideoExtra(videoUrl, ""))
        )
    }
}

sealed interface SoundState {

    object Empty : SoundState

    data class Content(
        val list: List<UiModel>,
        val soundDto: AmericanSoundDto,
    ) : SoundState
}