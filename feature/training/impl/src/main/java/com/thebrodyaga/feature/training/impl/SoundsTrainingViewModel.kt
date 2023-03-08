package com.thebrodyaga.feature.training.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thebrodyaga.data.sounds.api.SoundsRepository
import com.thebrodyaga.data.sounds.api.model.PracticeWordDto
import timber.log.Timber
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach

class SoundsTrainingViewModel @Inject constructor(
    private val repository: SoundsRepository
) : ViewModel() {

    private val state = MutableStateFlow<SoundsTrainingState>(SoundsTrainingState.Empty)
    fun getState() = state.asStateFlow()

    init {
        repository.getAllPracticeWords()
            .flowOn(Dispatchers.IO)
            .map { it.shuffled() }
            .onEach {
                state.value = SoundsTrainingState.Content(it)
            }
            .onCompletion { error ->
                if (error != null) Timber.e(error)
            }
            .launchIn(viewModelScope)
    }
}

sealed interface SoundsTrainingState {

    object Empty : SoundsTrainingState

    data class Content(
        val sounds: List<PracticeWordDto>
    ) : SoundsTrainingState
}