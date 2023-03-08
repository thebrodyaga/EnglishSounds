package com.thebrodyaga.feature.videoList.impl.listoflists

import com.thebrodyaga.englishsounds.base.app.BasePresenter
import com.thebrodyaga.feature.videoList.impl.interactor.AllVideoInteractor
import com.thebrodyaga.legacy.AdvancedExercisesVideoListItem
import com.thebrodyaga.legacy.ContrastingSoundVideoListItem
import com.thebrodyaga.legacy.MostCommonWordsVideoListItem
import com.thebrodyaga.legacy.SoundVideoListItem
import com.thebrodyaga.legacy.SoundsListItem
import com.thebrodyaga.legacy.VideoItemInList
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import timber.log.Timber
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach

@InjectViewState
class ListOfVideoListsPresenter @Inject constructor(
    private val videoInteractor: AllVideoInteractor
) : BasePresenter<ListOfVideoListsView>() {

    val positionList = mutableMapOf<Int, Pair<Int, Int>>()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        videoInteractor.getAllList()
            .map { list ->
                val result = mutableListOf<SoundsListItem>()

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
                }
                result
            }
            .flowOn(Dispatchers.IO)
            .onEach { viewState.setListData(it) }
            .onCompletion { it?.let { Timber.e(it) } }
            .launchIn(this)
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
}