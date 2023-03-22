package com.thebrodyaga.feature.videoList.impl.interactor

import com.thebrodyaga.data.sounds.api.SoundsRepository
import com.thebrodyaga.data.sounds.api.SoundsVideoRepository
import com.thebrodyaga.data.sounds.api.model.SoundType
import com.thebrodyaga.data.sounds.api.model.SoundVideoRes
import com.thebrodyaga.legacy.AdvancedExercisesVideoItem
import com.thebrodyaga.legacy.AdvancedExercisesVideoListItem
import com.thebrodyaga.legacy.ContrastingSoundVideoItem
import com.thebrodyaga.legacy.ContrastingSoundVideoListItem
import com.thebrodyaga.legacy.MostCommonWordsVideoItem
import com.thebrodyaga.legacy.MostCommonWordsVideoListItem
import com.thebrodyaga.legacy.SoundVideoItem
import com.thebrodyaga.legacy.SoundVideoListItem
import com.thebrodyaga.legacy.VideoListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach

class AllVideoInteractor constructor(
    private val soundsRepository: SoundsRepository,
    private val soundsVideoRepository: SoundsVideoRepository
) {

    private var allListCache = listOf<VideoListItem>()

    fun getAllList() =
        if (allListCache.isNotEmpty())
            flowOf(allListCache)
        else loadAddList()

    private fun loadAddList(): Flow<List<VideoListItem>> =
        soundsRepository.getAllSounds()
            .mapLatest { sounds ->
                val t1 = soundsVideoRepository.getContrastingSoundsVideo().first()
                val t2 = soundsVideoRepository.getMostCommonWordsVideo().first()
                val t3 = soundsVideoRepository.getAdvancedExercisesVideo().first()
                val t4 = soundsVideoRepository.getSoundsVideo().first()
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
            }.onEach { allListCache = it }
}