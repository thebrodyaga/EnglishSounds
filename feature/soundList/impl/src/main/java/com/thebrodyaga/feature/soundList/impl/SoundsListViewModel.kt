package com.thebrodyaga.feature.soundList.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thebrodyaga.brandbook.model.UiModel
import com.thebrodyaga.data.sounds.api.SoundsRepository
import com.thebrodyaga.data.sounds.api.SoundsVideoRepository
import com.thebrodyaga.data.sounds.api.model.AdvancedExercisesVideoRes
import com.thebrodyaga.data.sounds.api.model.AmericanSoundDto
import com.thebrodyaga.data.sounds.api.model.ContrastingSoundVideoRes
import com.thebrodyaga.data.sounds.api.model.MostCommonWordsVideoRes
import timber.log.Timber
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach

class SoundsListViewModel @Inject constructor(
    private val soundsRepository: SoundsRepository,
    private val soundsVideoRepository: SoundsVideoRepository,
    private val mapper: SoundListMapper,
) : ViewModel() {

    private val state = MutableStateFlow<SoundsListState>(SoundsListState.Empty)
    fun getState() = state.asStateFlow()
    val positionList = mutableMapOf<Int, Pair<Int, Int>>()

    init {
        combine(
            soundsRepository.getAllSounds().map { list -> list.associateBy { it.transcription } },
            soundsVideoRepository.getContrastingSoundsVideo(),
            soundsVideoRepository.getMostCommonWordsVideo(),
            soundsVideoRepository.getAdvancedExercisesVideo(),
        ) { sounds, contrastingSoundVideo, mostCommonWordsVideo, advancedExercisesVideo ->
            SoundsListBox(
                sounds,
                contrastingSoundVideo,
                mostCommonWordsVideo,
                advancedExercisesVideo
            )
        }.flowOn(Dispatchers.IO)
            .onEach { state.value = SoundsListState.Content(mapper.map(it.sounds)) }
            .onCompletion { it?.let { Timber.e(it) } }
            .launchIn(viewModelScope)
    }

    data class SoundsListBox(
        val sounds: Map<String, AmericanSoundDto>,
        val contrastingSoundVideo: List<ContrastingSoundVideoRes>,
        val mostCommonWordsVideo: List<MostCommonWordsVideoRes>,
        val advancedExercisesVideo: List<AdvancedExercisesVideoRes>
    )
}

sealed interface SoundsListState {

    object Empty : SoundsListState

    data class Content(
        val sounds: List<UiModel>
    ) : SoundsListState
}