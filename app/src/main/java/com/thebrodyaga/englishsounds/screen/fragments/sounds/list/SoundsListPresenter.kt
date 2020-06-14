package com.thebrodyaga.englishsounds.screen.fragments.sounds.list

import com.thebrodyaga.englishsounds.domine.entities.data.AmericanSoundDto
import com.thebrodyaga.englishsounds.domine.entities.data.SoundType
import com.thebrodyaga.englishsounds.domine.entities.resources.ContrastingSoundVideoRes
import com.thebrodyaga.englishsounds.domine.entities.ui.*
import com.thebrodyaga.englishsounds.repository.SoundsRepository
import com.thebrodyaga.englishsounds.repository.SoundsVideoRepository
import com.thebrodyaga.englishsounds.screen.base.BasePresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import timber.log.Timber
import javax.inject.Inject

@InjectViewState
class SoundsListPresenter @Inject constructor(
    private val soundsRepository: SoundsRepository,
    private val soundsVideoRepository: SoundsVideoRepository
) : BasePresenter<SoundsListView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        unSubscribeOnDestroy(
            Observable.combineLatest<Map<String, AmericanSoundDto>, Set<ContrastingSoundVideoRes>, SoundsListBox>(
                soundsRepository.getAllSounds()
                    .map { list -> list.map { it.transcription to it }.toMap() },
                soundsVideoRepository.getContrastingSoundsVideo().toObservable(),
                BiFunction { sounds, contrastingSoundVideo ->
                    SoundsListBox(sounds, contrastingSoundVideo)
                }
            ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ mapForUi(it) }, { Timber.e(it) })
        )
    }

    private fun mapForUi(box: SoundsListBox) {
        val sounds = box.sounds.values.sortedBy { it.transcription }
        val contrastingSoundVideo =
            box.contrastingSoundVideo.toList().sortedBy { it.firstTranscription }
        val vowelSounds = mutableListOf<AmericanSoundDto>()
        val rControlledVowels = mutableListOf<AmericanSoundDto>()
        val consonantSounds = mutableListOf<AmericanSoundDto>()
        sounds.forEach {
            when (it.soundType) {
                SoundType.CONSONANT_SOUND -> consonantSounds.add(it)
                SoundType.R_CONTROLLED_VOWELS -> rControlledVowels.add(it)
                SoundType.VOWEL_SOUNDS -> vowelSounds.add(it)
            }
        }
        val result = mutableListOf<SoundsListItem>()
        if (vowelSounds.isNotEmpty()) {
            result.add(SoundHeader(SoundType.VOWEL_SOUNDS))
            result.addAll(vowelSounds)
        }
        if (contrastingSoundVideo.isNotEmpty()) {
            result.add(
                ContrastingSoundVideoListItem(contrastingSoundVideo.map {
                    ContrastingSoundVideoItem(
                        it.videoId,
                        it.videoName,
                        box.sounds[it.firstTranscription],
                        box.sounds[it.secondTranscription]
                    )
                })
            )
        }
        if (rControlledVowels.isNotEmpty()) {
            result.add(SoundHeader(SoundType.R_CONTROLLED_VOWELS))
            result.addAll(rControlledVowels)
        }
        if (consonantSounds.isNotEmpty()) {
            result.add(SoundHeader(SoundType.CONSONANT_SOUND))
            result.addAll(consonantSounds)
        }
        viewState.setListData(result)
    }

    private data class SoundsListBox constructor(
        val sounds: Map<String, AmericanSoundDto>,
        val contrastingSoundVideo: Set<ContrastingSoundVideoRes>
    )
}