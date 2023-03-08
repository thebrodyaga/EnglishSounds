package com.thebrodyaga.feature.videoList.impl.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thebrodyaga.data.sounds.api.model.PracticeWordDto
import com.thebrodyaga.data.sounds.api.model.SoundType
import com.thebrodyaga.feature.videoList.api.VideoListType
import com.thebrodyaga.feature.videoList.impl.interactor.AllVideoInteractor
import com.thebrodyaga.legacy.AdvancedExercisesVideoListItem
import com.thebrodyaga.legacy.ContrastingSoundVideoListItem
import com.thebrodyaga.legacy.MostCommonWordsVideoListItem
import com.thebrodyaga.legacy.SoundVideoListItem
import com.thebrodyaga.legacy.VideoItemInList
import timber.log.Timber
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch

class VideoListViewModel @Inject constructor(
    private val videoInteractor: AllVideoInteractor,
    private var listType: VideoListType?,
) : ViewModel() {

    private val state = MutableStateFlow<VideoListState>(VideoListState.Empty)
    fun getState() = state.asStateFlow()

    init {
        viewModelScope.launch {
            val flow = videoInteractor.getAllList()
                .flatMapLatest { it.asFlow() }
                .filter {
                    when (listType) {
                        VideoListType.ContrastingSounds -> it is ContrastingSoundVideoListItem
                        VideoListType.MostCommonWords -> it is MostCommonWordsVideoListItem
                        VideoListType.AdvancedExercises -> it is AdvancedExercisesVideoListItem
                        VideoListType.VowelSounds -> it is SoundVideoListItem && it.soundType == SoundType.VOWEL_SOUNDS
                        VideoListType.RControlledVowels -> it is SoundVideoListItem && it.soundType == SoundType.R_CONTROLLED_VOWELS
                        VideoListType.ConsonantSounds -> it is SoundVideoListItem && it.soundType == SoundType.CONSONANT_SOUND
                        null -> false
                    }
                }
                .flatMapLatest { it.list.asFlow() }
                /*.map {
                val result = mutableListOf<VideoItemInList>()
                it.forEachIndexed { index, item ->
                    when {
                        index == 2 && index != it.lastIndex ->
                            result.add(AdItem(AdTag.SOUND_VIDEO_LIST, listType.name))
                        *//*index != 0 && index % 6 == 0 && index != it.lastIndex ->
                                result.add(AdItem(AdTag.SOUND_VIDEO_LIST, listType.name))*//*
                        }
                        result.add(item)
                    }
                    result
                }*/
                .flowOn(Dispatchers.IO)
                .onCompletion { it?.let { Timber.e(it) } }

            try {
                state.value = VideoListState.Content(flow.toList())
            } catch (e: Throwable) {
                Timber.e(e)
            }

        }
    }
}

sealed interface VideoListState {

    object Empty : VideoListState

    data class Content(
        val list: List<VideoItemInList>
    ) : VideoListState
}