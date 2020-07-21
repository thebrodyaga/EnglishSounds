package com.thebrodyaga.englishsounds.screen.fragments.video.listoflists

import com.thebrodyaga.englishsounds.domine.entities.data.AdTag
import com.thebrodyaga.englishsounds.domine.entities.data.SoundType
import com.thebrodyaga.englishsounds.domine.entities.ui.*
import com.thebrodyaga.englishsounds.domine.interactors.AllVideoInteractor
import com.thebrodyaga.englishsounds.screen.base.BasePresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import timber.log.Timber
import javax.inject.Inject

@InjectViewState
class ListOfVideoListsPresenter @Inject constructor(
    private val videoInteractor: AllVideoInteractor
) : BasePresenter<ListOfVideoListsView>() {

    val positionList = mutableMapOf<Int, Pair<Int, Int>>()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        unSubscribeOnDestroy(
            videoInteractor.getAllList()
                .map { list ->
                    val result = mutableListOf<SoundsListItem>()

                    list.forEachIndexed { index, videoItems ->
                        if (index == 2) {
                            result.add(AdItem(AdTag.SOUND_LIST_OF_VIDEO_LIST))
                        }
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
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    viewState.setListData(it)
                }, { Timber.e(it) })
        )
    }

    private inline fun <T> List<T>.mapOrAd(
        transform: (T) -> VideoItemInList,
        customAdTag: String? = null
    ): List<VideoItemInList> {
        val result = mutableListOf<VideoItemInList>()
        forEachIndexed { index, item ->
            when {
                index == 2 && index != lastIndex ->
                    result.add(AdItem(AdTag.SOUND_VIDEO_LIST, customAdTag))
                index != 0 && index % 6 == 0 && index != lastIndex ->
                    result.add(AdItem(AdTag.SOUND_VIDEO_LIST, customAdTag))
            }
            result.add(transform(item))
        }
        return result
    }
}