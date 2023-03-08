package com.thebrodyaga.feature.training.impl

import com.thebrodyaga.data.sounds.api.model.PracticeWordDto
import com.thebrodyaga.data.sounds.api.SoundsRepository
import com.thebrodyaga.englishsounds.base.app.BasePresenter
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
class SoundsTrainingPresenter @Inject constructor(
    private val repository: SoundsRepository
) : BasePresenter<SoundsTrainingView>() {

    private var currentList = listOf<PracticeWordDto>()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        repository.getAllPracticeWords()
            .flowOn(Dispatchers.IO)
            .map { it.shuffled() }
            .onEach {
                currentList = it
                viewState.setData(it)
            }
            .onCompletion { error ->
                if (error != null) Timber.e(error)
            }
            .launchIn(this)
    }
}