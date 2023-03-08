package com.thebrodyaga.feature.soundDetails.impl.ui

import com.thebrodyaga.data.sounds.api.model.AmericanSoundDto
import com.thebrodyaga.data.sounds.api.SoundsRepository
import com.thebrodyaga.englishsounds.base.app.BasePresenter
import com.thebrodyaga.legacy.ShortAdItem
import com.thebrodyaga.legacy.SoundsDetailsWithAd
import com.thebrodyaga.legacy.WordsHeader
import com.thebrodyaga.legacy.data.AdTag
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import timber.log.Timber
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach

@InjectViewState
class SoundPresenter @Inject constructor(
    private val repository: SoundsRepository
) : BasePresenter<SoundView>() {
    lateinit var transcription: String

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
            repository.getSounds(transcription)
                .flowOn(Dispatchers.IO)
                .onEach { mapForUi(it) }
                .onCompletion { it?.let { Timber.e(it) } }
                .launchIn(this)
    }


    private fun mapForUi(soundDto: AmericanSoundDto) {
        val result = mutableListOf<Any>()
        result.add(SoundsDetailsWithAd(soundDto, ShortAdItem(AdTag.SOUND_DETAILS)))
        if (soundDto.spellingWordList.isNotEmpty()) {
            result.add(WordsHeader(WordsHeader.Type.SPELLING))
//            result.add(ShortAdItem(AdTag.SOUND_DETAILS))
            result.addAll(soundDto.spellingWordList)
        }
        if (soundDto.soundPracticeWords.beginningSound.isNotEmpty()) {
            result.add(WordsHeader(WordsHeader.Type.BEGINNING_SOUND))
//            result.add(ShortAdItem(AdTag.SOUND_DETAILS_SECOND))
            result.addAll(soundDto.soundPracticeWords.beginningSound)
        }
        if (soundDto.soundPracticeWords.middleSound.isEmpty()) {
            result.add(WordsHeader(WordsHeader.Type.MIDDLE_SOUND))
//            result.add(ShortAdItem(AdTag.SOUND_DETAILS_THIRD))
            result.addAll(soundDto.soundPracticeWords.middleSound)
        }
        if (soundDto.soundPracticeWords.endSound.isNotEmpty()) {
            result.add(WordsHeader(WordsHeader.Type.END_SOUND))
//            result.add(ShortAdItem(AdTag.SOUND_DETAILS_FOURTH))
            result.addAll(soundDto.soundPracticeWords.endSound)
        }
        viewState.setData(result, soundDto)
    }
}