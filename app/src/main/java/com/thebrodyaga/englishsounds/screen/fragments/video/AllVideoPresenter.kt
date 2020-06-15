package com.thebrodyaga.englishsounds.screen.fragments.video

import com.thebrodyaga.englishsounds.repository.SoundsRepository
import com.thebrodyaga.englishsounds.repository.SoundsVideoRepository
import com.thebrodyaga.englishsounds.screen.base.BasePresenter
import moxy.InjectViewState
import javax.inject.Inject

@InjectViewState
class AllVideoPresenter @Inject constructor(
    private val soundsRepository: SoundsRepository,
    private val videoRepository: SoundsVideoRepository
) : BasePresenter<AllVideoView>() {
}