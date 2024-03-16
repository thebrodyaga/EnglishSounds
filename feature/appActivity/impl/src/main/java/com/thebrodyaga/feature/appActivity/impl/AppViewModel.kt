package com.thebrodyaga.feature.appActivity.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thebrodyaga.data.sounds.api.SoundsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.take
import timber.log.Timber
import javax.inject.Inject

class AppViewModel @Inject constructor(
    private val soundsRepository: SoundsRepository,
) : ViewModel() {
    private var _isReady: Boolean = false
    val isReady: Boolean
        get() = _isReady

    init {
        soundsRepository.getAllSounds()
            .flowOn(Dispatchers.IO)
            .take(1)
            .onCompletion { error ->
                if (error != null) Timber.e(error)
                _isReady = true
            }
            .launchIn(viewModelScope)
    }

    val keepSystemSplashVisible
        get() = soundsRepository.isWasExistSoundsInInternalStorage
}