package com.thebrodyaga.englishsounds.screen.fragments.sounds.training

import com.thebrodyaga.data.sounds.api.model.PracticeWordDto
import com.thebrodyaga.data.sounds.api.SoundsRepository
import com.thebrodyaga.englishsounds.screen.base.BasePresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import timber.log.Timber
import javax.inject.Inject

@InjectViewState
class SoundsTrainingPresenter @Inject constructor(
    private val repository: SoundsRepository
) : BasePresenter<SoundsTrainingView>() {

    private var currentList = listOf<PracticeWordDto>()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        unSubscribeOnDestroy(
            repository.getAllPracticeWords()
                .subscribeOn(Schedulers.io())
                .map { it.shuffled() }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        currentList = it
                        viewState.setData(it)
                    }
                    , { Timber.e(it) })
        )
    }
}