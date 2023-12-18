package com.thebrodyaga.feature.videoList.impl.carousel

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.analytics.FirebaseAnalytics
import com.thebrodyaga.ad.api.AppAdLoader
import com.thebrodyaga.base.navigation.api.RouterProvider
import com.thebrodyaga.brandbook.component.sound.mini.SoundCardMiniUiModel
import com.thebrodyaga.brandbook.model.UiModel
import com.thebrodyaga.data.sounds.api.model.AmericanSoundDto
import com.thebrodyaga.data.sounds.api.model.SoundType
import com.thebrodyaga.englishsounds.analytics.AnalyticsEngine
import com.thebrodyaga.feature.soundDetails.api.SoundDetailsScreenFactory
import com.thebrodyaga.feature.videoList.api.VideoListType
import com.thebrodyaga.feature.videoList.api.VideoScreenFactory
import com.thebrodyaga.feature.videoList.impl.interactor.AllVideoInteractor
import com.thebrodyaga.feature.youtube.api.PlayVideoExtra
import com.thebrodyaga.feature.youtube.api.YoutubeScreenFactory
import com.thebrodyaga.legacy.AdvancedExercisesVideoListItem
import com.thebrodyaga.legacy.ContrastingSoundVideoListItem
import com.thebrodyaga.legacy.MostCommonWordsVideoListItem
import com.thebrodyaga.legacy.SoundVideoListItem
import com.thebrodyaga.legacy.VideoListItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

class VideoCarouselViewModel @Inject constructor(
    private val videoInteractor: AllVideoInteractor,
    private val routerProvider: RouterProvider,
    private val soundScreenFactory: SoundDetailsScreenFactory,
    private val videoScreenFactory: VideoScreenFactory,
    private val youtubeScreenFactory: YoutubeScreenFactory,
    private val mapper: VideoCarouselMapper,
    private val adLoader: AppAdLoader,
) : ViewModel() {

    private val state = MutableStateFlow<ListOfVideoListsState>(ListOfVideoListsState.Empty)
    fun getState() = state.asStateFlow()

    init {
        videoInteractor.getAllList().combine(adLoader.videoListAd) { list, ad -> list to ad }
            .map {
                val (list, ad) = it
                mapper.mapUi(list, ad)
            }
            .flowOn(Dispatchers.IO)
            .onEach { state.value = ListOfVideoListsState.Content(it) }
            .onCompletion { it?.let { Timber.e(it) } }
            .launchIn(viewModelScope)
    }

    fun onSoundClick(model: SoundCardMiniUiModel) {
        val sound = model.payload as? AmericanSoundDto ?: return
        routerProvider.anyRouter.navigateTo(
            soundScreenFactory.soundDetailsScreen(sound.transcription),
        )

        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, sound.transcription)
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, sound.name)
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "sound")
        AnalyticsEngine.logEvent(
            FirebaseAnalytics.Event.SELECT_CONTENT,
            bundle
        )
    }

    fun onShowAllVideoClick(videoListItem: VideoListItem) {
        val showPage: VideoListType = when (videoListItem) {
            is ContrastingSoundVideoListItem -> VideoListType.ContrastingSounds
            is MostCommonWordsVideoListItem -> VideoListType.MostCommonWords
            is AdvancedExercisesVideoListItem -> VideoListType.AdvancedExercises
            is SoundVideoListItem -> when (videoListItem.soundType) {
                SoundType.CONSONANT_SOUND -> VideoListType.ConsonantSounds
                SoundType.R_CONTROLLED_VOWELS -> VideoListType.RControlledVowels
                SoundType.VOWEL_SOUNDS -> VideoListType.VowelSounds
            }
        }
        routerProvider.anyRouter.navigateTo(videoScreenFactory.allVideoScreen(showPage))
    }

    fun onVideoClick(videoId: String) {
        routerProvider.anyRouter.navigateTo(
            youtubeScreenFactory.youtubeScreen(PlayVideoExtra(videoId, ""))
        )
    }
}

sealed interface ListOfVideoListsState {

    object Empty : ListOfVideoListsState

    data class Content(
        val list: List<UiModel>,
    ) : ListOfVideoListsState
}