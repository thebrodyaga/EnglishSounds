package com.thebrodyaga.feature.soundDetails.impl.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thebrodyaga.data.sounds.api.SoundsRepository
import com.thebrodyaga.data.sounds.api.model.AmericanSoundDto
import com.thebrodyaga.legacy.ShortAdItem
import com.thebrodyaga.legacy.SoundsDetailsWithAd
import com.thebrodyaga.legacy.WordsHeader
import com.thebrodyaga.legacy.data.AdTag
import timber.log.Timber
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach

class SoundViewModel @Inject constructor(
    private val repository: SoundsRepository,
    val transcription: String,
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
        val result = mutableListOf<Any>()
        result.add(SoundsDetailsWithAd(soundDto, ShortAdItem(AdTag.SOUND_DETAILS)))
        if (soundDto.spellingWordList.isNotEmpty()) {
            result.add(WordsHeader(WordsHeader.Type.SPELLING))
//            result.add(ShortAdItem(AdTag.SOUND_DETAILS))
            result.addAll(soundDto.spellingWordList)
        }
        if (soundDto.soundPracticeWords.beginningSound.isNotEmpty()) {
            result.add(WordsHeader(WordsHeader.Type.BEGINNING_SOUND))
//            result.add(ShortAdItem(AdTag.SOUND_DETAILS_SECOND))
            result.addAll(soundDto.soundPracticeWords.beginningSound)
        }
        if (soundDto.soundPracticeWords.middleSound.isEmpty()) {
            result.add(WordsHeader(WordsHeader.Type.MIDDLE_SOUND))
//            result.add(ShortAdItem(AdTag.SOUND_DETAILS_THIRD))
            result.addAll(soundDto.soundPracticeWords.middleSound)
        }
        if (soundDto.soundPracticeWords.endSound.isNotEmpty()) {
            result.add(WordsHeader(WordsHeader.Type.END_SOUND))
//            result.add(ShortAdItem(AdTag.SOUND_DETAILS_FOURTH))
            result.addAll(soundDto.soundPracticeWords.endSound)
        }
        state.value = SoundState.Content(result, soundDto)
    }
}

sealed interface SoundState {

    object Empty : SoundState

    data class Content(
        val list: List<Any>,
        val soundDto: AmericanSoundDto,
    ) : SoundState
}