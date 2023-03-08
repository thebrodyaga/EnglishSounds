package com.thebrodyaga.feature.soundList.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thebrodyaga.data.sounds.api.SoundsRepository
import com.thebrodyaga.data.sounds.api.SoundsVideoRepository
import com.thebrodyaga.data.sounds.api.model.AdvancedExercisesVideoRes
import com.thebrodyaga.data.sounds.api.model.AmericanSoundDto
import com.thebrodyaga.data.sounds.api.model.ContrastingSoundVideoRes
import com.thebrodyaga.data.sounds.api.model.MostCommonWordsVideoRes
import com.thebrodyaga.data.sounds.api.model.SoundType
import com.thebrodyaga.legacy.SoundHeader
import timber.log.Timber
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
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
            .onEach { mapForUi(it) }
            .onCompletion { it?.let { Timber.e(it) } }
            .launchIn(viewModelScope)
    }

    private fun mapForUi(box: SoundsListBox) {
        val sounds = box.sounds.values.sortedBy { it.transcription }
        val contrastingSoundVideo = box.contrastingSoundVideo
        val mostCommonWordsVideo = box.mostCommonWordsVideo
        val advancedExercisesVideo = box.advancedExercisesVideo
        val vowelSounds = mutableListOf<AmericanSoundDto>()
        val rControlledVowels = mutableListOf<AmericanSoundDto>()
        val consonantSounds = mutableListOf<AmericanSoundDto>()
        sounds.forEach {
            when (it.soundType) {
                SoundType.CONSONANT_SOUND -> consonantSounds.add(it)
                SoundType.R_CONTROLLED_VOWELS -> rControlledVowels.add(it)
                SoundType.VOWEL_SOUNDS -> vowelSounds.add(it)
            }
        }
        val result = mutableListOf<Any>()
        if (vowelSounds.isNotEmpty()) {
            result.add(SoundHeader(SoundType.VOWEL_SOUNDS))
            result.addAll(vowelSounds)
        }
//        result.add(AdItem(AdTag.SOUNDS_FIRST))
        if (rControlledVowels.isNotEmpty()) {
            result.add(SoundHeader(SoundType.R_CONTROLLED_VOWELS))
            result.addAll(rControlledVowels)
        }
//        result.add(ShortAdItem(AdTag.SOUNDS_SECOND))
        if (consonantSounds.isNotEmpty()) {
            result.add(SoundHeader(SoundType.CONSONANT_SOUND))
            result.addAll(consonantSounds)
        }
        state.value = SoundsListState.Content(result)
    }

    private data class SoundsListBox(
        val sounds: Map<String, AmericanSoundDto>,
        val contrastingSoundVideo: List<ContrastingSoundVideoRes>,
        val mostCommonWordsVideo: List<MostCommonWordsVideoRes>,
        val advancedExercisesVideo: List<AdvancedExercisesVideoRes>
    )
}

sealed interface SoundsListState {

    object Empty : SoundsListState

    data class Content(
        val sounds: List<Any>
    ) : SoundsListState
}