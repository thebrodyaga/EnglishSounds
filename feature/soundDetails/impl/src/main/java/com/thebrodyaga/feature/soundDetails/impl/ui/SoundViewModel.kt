package com.thebrodyaga.feature.soundDetails.impl.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thebrodyaga.brandbook.model.UiModel
import com.thebrodyaga.data.sounds.api.SoundsRepository
import com.thebrodyaga.data.sounds.api.model.AmericanSoundDto
import com.thebrodyaga.feature.soundDetails.impl.ui.mapper.SoundDetailsMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

class SoundViewModel @Inject constructor(
    private val repository: SoundsRepository,
    private val mapper: SoundDetailsMapper,
    private val transcription: String,
) : ViewModel() {

    private val state = MutableStateFlow<SoundState>(SoundState.Empty)
    fun getState() = state.asStateFlow()

    init {
        repository.getSounds(transcription)
            .flowOn(Dispatchers.IO)
            .onEach { mapForUi(it) }
            .onCompletion { it?.let { Timber.e(it) } }
            .launchIn(viewModelScope)
    }

    private fun mapForUi(soundDto: AmericanSoundDto) {
        state.value = SoundState.Content(mapper.mapFullList(soundDto), soundDto)
    }
}

sealed interface SoundState {

    object Empty : SoundState

    data class Content(
        val list: List<UiModel>,
        val soundDto: AmericanSoundDto,
    ) : SoundState
}