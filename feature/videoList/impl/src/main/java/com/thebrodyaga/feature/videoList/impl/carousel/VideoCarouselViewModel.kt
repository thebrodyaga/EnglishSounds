package com.thebrodyaga.feature.videoList.impl.carousel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thebrodyaga.brandbook.model.UiModel
import com.thebrodyaga.feature.videoList.impl.interactor.AllVideoInteractor
import com.thebrodyaga.legacy.AdvancedExercisesVideoListItem
import com.thebrodyaga.legacy.ContrastingSoundVideoListItem
import com.thebrodyaga.legacy.MostCommonWordsVideoListItem
import com.thebrodyaga.legacy.SoundVideoListItem
import com.thebrodyaga.legacy.VideoItemInList
import com.thebrodyaga.legacy.VideoListItem
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

class ListOfVideoListsViewModel @Inject constructor(
    private val videoInteractor: AllVideoInteractor
) : ViewModel() {

    val positionList = mutableMapOf<Int, Pair<Int, Int>>()
    private val state = MutableStateFlow<ListOfVideoListsState>(ListOfVideoListsState.Empty)
    fun getState() = state.asStateFlow()

    init {
        videoInteractor.getAllList()
            .map { list: List<VideoListItem> ->
                mapUi(list)
                /*list.forEachIndexed { index, videoItems ->
                    *//*if (index == 2) {
                        result.add(AdItem(AdTag.SOUND_LIST_OF_VIDEO_LIST))
                    }*//*
                    val adTag = when (videoItems) {
                        is ContrastingSoundVideoListItem -> "ContrastingSoundVideoListItem"
                        is MostCommonWordsVideoListItem -> "MostCommonWordsVideoListItem"
                        is AdvancedExercisesVideoListItem -> "AdvancedExercisesVideoListItem"
                        is SoundVideoListItem -> videoItems.soundType.name
                    }
                    result.add(
                        when (videoItems) {
                            is ContrastingSoundVideoListItem -> ContrastingSoundVideoListItem(
                                videoItems.list.mapOrAd({ it }, adTag)
                            )

                            is MostCommonWordsVideoListItem -> MostCommonWordsVideoListItem(
                                videoItems.list.mapOrAd({ it }, adTag)
                            )

                            is AdvancedExercisesVideoListItem -> AdvancedExercisesVideoListItem(
                                videoItems.list.mapOrAd({ it }, adTag)
                            )

                            is SoundVideoListItem -> SoundVideoListItem(
                                videoItems.soundType,
                                videoItems.list.mapOrAd({ it }, adTag)
                            )
                        }
                    )
                }*/
            }
            .flowOn(Dispatchers.IO)
            .onEach { state.value = ListOfVideoListsState.Content(it) }
            .onCompletion { it?.let { Timber.e(it) } }
            .launchIn(viewModelScope)
    }

    private inline fun <T> List<T>.mapOrAd(
        transform: (T) -> VideoItemInList,
        customAdTag: String? = null
    ): List<VideoItemInList> {
        val result = mutableListOf<VideoItemInList>()
        forEachIndexed { index, item ->
            /*when {
                index == 2 && index != lastIndex ->
                    result.add(AdItem(AdTag.SOUND_VIDEO_LIST, customAdTag))
                index != 0 && index % 6 == 0 && index != lastIndex ->
                    result.add(AdItem(AdTag.SOUND_VIDEO_LIST, customAdTag))
            }*/
            result.add(transform(item))
        }
        return result
    }

    private fun mapUi(list: List<VideoListItem>) = buildList<UiModel> {
        list.forEachIndexed { index, videoItems ->
            /*if (index == 2) {
                result.add(AdItem(AdTag.SOUND_LIST_OF_VIDEO_LIST))
            }*/
            val adTag = when (videoItems) {
                is ContrastingSoundVideoListItem -> "ContrastingSoundVideoListItem"
                is MostCommonWordsVideoListItem -> "MostCommonWordsVideoListItem"
                is AdvancedExercisesVideoListItem -> "AdvancedExercisesVideoListItem"
                is SoundVideoListItem -> videoItems.soundType.name
            }
            add(VideoCarouselUiModel(videoItems.list.map {
                VideoCarouselItemUiModel(it.videoId, it.title)
            }))
            /*when (videoItems) {
                is ContrastingSoundVideoListItem -> VideoCarouselUiModel(videoItems.list.map {
                    VideoCarouselItemUiModel(it.videoId, it.title)
                })
                    ContrastingSoundVideoListItem(
                    videoItems.list.mapOrAd({ it }, adTag)
                )

                is MostCommonWordsVideoListItem -> MostCommonWordsVideoListItem(
                    videoItems.list.mapOrAd({ it }, adTag)
                )

                is AdvancedExercisesVideoListItem -> AdvancedExercisesVideoListItem(
                    videoItems.list.mapOrAd({ it }, adTag)
                )

                is SoundVideoListItem -> SoundVideoListItem(
                    videoItems.soundType,
                    videoItems.list.mapOrAd({ it }, adTag)
                )
            }*/

        }
    }
}

sealed interface ListOfVideoListsState {

    object Empty : ListOfVideoListsState

    data class Content(
        val list: List<UiModel>
    ) : ListOfVideoListsState
}