package com.thebrodyaga.feature.videoList.impl.page

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.analytics.FirebaseAnalytics
import com.thebrodyaga.base.navigation.api.RouterProvider
import com.thebrodyaga.brandbook.component.sound.mini.SoundCardMiniUiModel
import com.thebrodyaga.brandbook.model.UiModel
import com.thebrodyaga.data.sounds.api.model.AmericanSoundDto
import com.thebrodyaga.data.sounds.api.model.SoundType
import com.thebrodyaga.englishsounds.analytics.AnalyticsEngine
import com.thebrodyaga.feature.soundDetails.api.SoundDetailsScreenFactory
import com.thebrodyaga.feature.videoList.api.VideoListType
import com.thebrodyaga.feature.videoList.impl.carousel.VideoCarouselMapper
import com.thebrodyaga.feature.videoList.impl.interactor.AllVideoInteractor
import com.thebrodyaga.feature.youtube.api.PlayVideoExtra
import com.thebrodyaga.feature.youtube.api.YoutubeScreenFactory
import com.thebrodyaga.legacy.AdvancedExercisesVideoListItem
import com.thebrodyaga.legacy.ContrastingSoundVideoListItem
import com.thebrodyaga.legacy.MostCommonWordsVideoListItem
import com.thebrodyaga.legacy.SoundVideoListItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

class VideoListViewModel @Inject constructor(
    private val videoInteractor: AllVideoInteractor,
    private var listType: VideoListType?,
    private var mapper: VideoCarouselMapper,
    private val routerProvider: RouterProvider,
    private val soundScreenFactory: SoundDetailsScreenFactory,
    private val youtubeScreenFactory: YoutubeScreenFactory,
) : ViewModel() {

    private val state = MutableStateFlow<VideoListState>(VideoListState.Empty)
    fun getState() = state.asStateFlow()

    init {
        videoInteractor.getAllList()
            .flowOn(Dispatchers.IO)
            .onEach { allVideo ->
                allVideo.filter {
                    when (listType) {
                        VideoListType.ContrastingSounds -> it is ContrastingSoundVideoListItem
                        VideoListType.MostCommonWords -> it is MostCommonWordsVideoListItem
                        VideoListType.AdvancedExercises -> it is AdvancedExercisesVideoListItem
                        VideoListType.VowelSounds -> it is SoundVideoListItem && it.soundType == SoundType.VOWEL_SOUNDS
                        VideoListType.RControlledVowels -> it is SoundVideoListItem && it.soundType == SoundType.R_CONTROLLED_VOWELS
                        VideoListType.ConsonantSounds -> it is SoundVideoListItem && it.soundType == SoundType.CONSONANT_SOUND
                        null -> false
                    }
                }.onEach { videoList ->
                    val uiList = videoList.list.map { video -> mapper.mapVideoItemUiModel(video) }
                    state.value = VideoListState.Content(uiList)
                }
            }
            .catch { Timber.e(it) }
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

    fun onVideoClick(videoId: String) {
        routerProvider.anyRouter.navigateTo(
            youtubeScreenFactory.youtubeScreen(PlayVideoExtra(videoId, ""))
        )
    }
}

sealed interface VideoListState {

    object Empty : VideoListState

    data class Content(
        val list: List<UiModel>
    ) : VideoListState
}