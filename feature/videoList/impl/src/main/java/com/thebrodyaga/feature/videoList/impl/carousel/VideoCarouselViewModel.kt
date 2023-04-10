package com.thebrodyaga.feature.videoList.impl.carousel

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.analytics.FirebaseAnalytics
import com.thebrodyaga.base.navigation.api.RouterProvider
import com.thebrodyaga.base.navigation.impl.transition.sharedElementBox
import com.thebrodyaga.brandbook.component.sound.mini.SoundCardMiniUiModel
import com.thebrodyaga.brandbook.model.UiModel
import com.thebrodyaga.data.sounds.api.model.AmericanSoundDto
import com.thebrodyaga.data.sounds.api.model.SoundType
import com.thebrodyaga.englishsounds.analytics.AnalyticsEngine
import com.thebrodyaga.feature.soundDetails.api.SoundDetailsScreenFactory
import com.thebrodyaga.feature.videoList.api.VideoListType
import com.thebrodyaga.feature.videoList.api.VideoScreenFactory
import com.thebrodyaga.feature.videoList.impl.interactor.AllVideoInteractor
import com.thebrodyaga.legacy.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

class VideoCarouselViewModel @Inject constructor(
    private val videoInteractor: AllVideoInteractor,
    private val routerProvider: RouterProvider,
    private val soundScreenFactory: SoundDetailsScreenFactory,
    private val videoScreenFactory: VideoScreenFactory,
    private val mapper: VideoCarouselMapper,
) : ViewModel() {

    private val state = MutableStateFlow<ListOfVideoListsState>(ListOfVideoListsState.Empty)
    fun getState() = state.asStateFlow()

    init {
        videoInteractor.getAllList()
            .map { list: List<VideoListItem> ->
                mapper.mapUi(list)
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
}

sealed interface ListOfVideoListsState {

    object Empty : ListOfVideoListsState

    data class Content(
        val list: List<UiModel>
    ) : ListOfVideoListsState
}