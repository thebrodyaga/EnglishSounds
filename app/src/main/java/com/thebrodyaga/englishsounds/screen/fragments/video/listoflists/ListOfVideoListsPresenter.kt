package com.thebrodyaga.englishsounds.screen.fragments.video.listoflists

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
                /*.map { list ->
                    val result = mutableListOf<SoundsListItem>()
                    val contrastingSoundVideo: List<ContrastingSoundVideoListItem>
                    val mostCommonWordsVideo: List<MostCommonWordsVideoListItem>
                    val advancedExercisesVideo: List<AdvancedExercisesVideoListItem>
                    val consonantSoundsVideo: List<SoundVideoListItem>
                    val rControlledVowelsVideo: List<SoundVideoListItem>
                    val vowelSoundsVideo: List<SoundVideoListItem>

                    list.forEach {
                        when (it) {

                        }
                    }
                    result
                }*/
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    viewState.setListData(it)
                }, { Timber.e(it) })
        )
    }
}