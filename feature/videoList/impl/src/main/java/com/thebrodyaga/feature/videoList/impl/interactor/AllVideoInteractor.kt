package com.thebrodyaga.feature.videoList.impl.interactor

import com.thebrodyaga.data.sounds.api.model.SoundType
import com.thebrodyaga.data.sounds.api.model.AdvancedExercisesVideoRes
import com.thebrodyaga.data.sounds.api.model.ContrastingSoundVideoRes
import com.thebrodyaga.data.sounds.api.model.MostCommonWordsVideoRes
import com.thebrodyaga.data.sounds.api.model.SoundVideoRes
import com.thebrodyaga.data.sounds.api.SoundsRepository
import com.thebrodyaga.data.sounds.api.SoundsVideoRepository
import com.thebrodyaga.legacy.AdvancedExercisesVideoItem
import com.thebrodyaga.legacy.AdvancedExercisesVideoListItem
import com.thebrodyaga.legacy.ContrastingSoundVideoItem
import com.thebrodyaga.legacy.ContrastingSoundVideoListItem
import com.thebrodyaga.legacy.MostCommonWordsVideoItem
import com.thebrodyaga.legacy.MostCommonWordsVideoListItem
import com.thebrodyaga.legacy.SoundVideoItem
import com.thebrodyaga.legacy.SoundVideoListItem
import com.thebrodyaga.legacy.VideoListItem
import io.reactivex.Observable
import io.reactivex.functions.Function4

class AllVideoInteractor constructor(
    private val soundsRepository: SoundsRepository,
    private val soundsVideoRepository: SoundsVideoRepository
) {

    private var allListCache = listOf<VideoListItem>()

    fun getAllList() =
        if (allListCache.isNotEmpty())
            Observable.just(allListCache)
        else loadAddList()

    private fun loadAddList(): Observable<List<VideoListItem>> =
        soundsRepository.getAllSounds()
            .flatMap { sounds ->
                Observable.combineLatest<List<ContrastingSoundVideoRes>, List<MostCommonWordsVideoRes>, List<AdvancedExercisesVideoRes>, List<SoundVideoRes>, List<VideoListItem>>(
                    soundsVideoRepository.getContrastingSoundsVideo().toObservable(),
                    soundsVideoRepository.getMostCommonWordsVideo().toObservable(),
                    soundsVideoRepository.getAdvancedExercisesVideo().toObservable(),
                    soundsVideoRepository.getSoundsVideo().toObservable(),
                    Function4 { t1, t2, t3, t4 ->
                        val result = mutableListOf<VideoListItem>()

                        val contrastingSounds = t1.map { video ->
                            ContrastingSoundVideoItem(
                                video.videoId,
                                video.videoName,
                                sounds.find { it.transcription == video.firstTranscription },
                                sounds.find { it.transcription == video.secondTranscription })
                        }
                        result.add(ContrastingSoundVideoListItem(contrastingSounds))

                        val mostCommonWords = t2.map { video ->
                            MostCommonWordsVideoItem(
                                video.videoId,
                                video.videoName
                            )
                        }
                        result.add(MostCommonWordsVideoListItem(mostCommonWords))

                        val advancedExercises = t3.map { video ->
                            AdvancedExercisesVideoItem(
                                video.videoId,
                                video.videoName,
                                sounds.find { it.transcription == video.firstTranscription },
                                sounds.find { it.transcription == video.secondTranscription }
                            )
                        }

                        result.add(AdvancedExercisesVideoListItem(advancedExercises))

                        fun mapToSoundVideoItem(video: SoundVideoRes) = SoundVideoItem(
                            video,
                            sounds.find { it.transcription == video.transcription },
                            video.videoName
                        )

                        fun mapToSoundVideoListItem(soundType: SoundType) =
                            SoundVideoListItem(
                                soundType,
                                t4.filter { it.soundType == soundType }
                                    .map { mapToSoundVideoItem(it) })


                        result.add(mapToSoundVideoListItem(SoundType.VOWEL_SOUNDS))
                        result.add(mapToSoundVideoListItem(SoundType.R_CONTROLLED_VOWELS))
                        result.add(mapToSoundVideoListItem(SoundType.CONSONANT_SOUND))

                        result
                    }
                )
            }.doOnNext { allListCache = it }
}