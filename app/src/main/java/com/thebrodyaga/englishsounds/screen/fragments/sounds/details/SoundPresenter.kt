package com.thebrodyaga.englishsounds.screen.fragments.sounds.details

import com.thebrodyaga.englishsounds.domine.entities.data.AdTag
import com.thebrodyaga.englishsounds.domine.entities.data.AmericanSoundDto
import com.thebrodyaga.englishsounds.domine.entities.ui.ShortAdItem
import com.thebrodyaga.englishsounds.domine.entities.ui.SoundsDetailsListItem
import com.thebrodyaga.englishsounds.domine.entities.ui.SoundsDetailsWithAd
import com.thebrodyaga.englishsounds.domine.entities.ui.WordsHeader
import com.thebrodyaga.englishsounds.repository.SoundsRepository
import com.thebrodyaga.englishsounds.screen.base.BasePresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import timber.log.Timber
import javax.inject.Inject

@InjectViewState
class SoundPresenter @Inject constructor(
    private val repository: SoundsRepository
) : BasePresenter<SoundView>() {
    lateinit var transcription: String

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        unSubscribeOnDestroy(
            repository.getSounds(transcription)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ mapForUi(it) }, { Timber.e(it) })
        )
    }


    private fun mapForUi(soundDto: AmericanSoundDto) {
        val result = mutableListOf<SoundsDetailsListItem>()
//        result.add(SoundsDetailsWithAd(soundDto, ShortAdItem(AdTag.SOUND_DETAILS)))
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