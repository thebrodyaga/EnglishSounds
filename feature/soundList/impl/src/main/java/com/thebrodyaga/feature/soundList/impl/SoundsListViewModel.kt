package com.thebrodyaga.feature.soundList.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thebrodyaga.ad.api.AppAd
import com.thebrodyaga.data.sounds.api.SoundsRepository
import com.thebrodyaga.data.sounds.api.model.AmericanSoundDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

class SoundsListViewModel @Inject constructor(
    private val soundsRepository: SoundsRepository,
) : ViewModel() {

    private val state = MutableStateFlow<SoundsListState>(SoundsListState.Empty)
    fun getState() = state.asStateFlow()

    init {
        soundsRepository.getAllSounds()
            .map { list -> list.associateBy { it.transcription } }
            .flowOn(Dispatchers.IO)
            .onEach { state.value = SoundsListState.Content(it) }
            .onCompletion { it?.let { Timber.e(it) } }
            .launchIn(viewModelScope)
    }

    data class SoundsListBox(
        val sounds: Map<String, AmericanSoundDto>,
        val firstAd: AppAd,
        val secondAd: AppAd,
    )
}

sealed interface SoundsListState {

    object Empty : SoundsListState

    data class Content(
        val sounds: Map<String, AmericanSoundDto>,
    ) : SoundsListState
}