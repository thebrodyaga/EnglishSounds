package com.thebrodyaga.englishsounds.repository.impl

import android.content.Context
import com.thebrodyaga.englishsounds.R
import com.thebrodyaga.data.sounds.api.model.SoundType
import com.thebrodyaga.data.sounds.api.model.AdvancedExercisesVideoRes
import com.thebrodyaga.data.sounds.api.model.ContrastingSoundVideoRes
import com.thebrodyaga.data.sounds.api.model.MostCommonWordsVideoRes
import com.thebrodyaga.data.sounds.api.model.SoundVideoRes
import com.thebrodyaga.data.sounds.api.SoundsVideoRepository
import io.reactivex.Single
import java.lang.IllegalArgumentException

class SoundsVideoRepositoryImpl constructor(
    val context: Context
) : SoundsVideoRepository {

    override fun getSoundsVideo(): Single<List<SoundVideoRes>> =
        getSoundsVideoFromRes()

    override fun getContrastingSoundsVideo(): Single<List<ContrastingSoundVideoRes>> =
        getContrastingSoundsVideoFromRes()

    override fun getMostCommonWordsVideo(): Single<List<MostCommonWordsVideoRes>> =
        getMostCommonWordsVideoFromRes()

    override fun getAdvancedExercisesVideo(): Single<List<AdvancedExercisesVideoRes>> =
        getAdvancedExercisesVideoFromRes()

    private fun getSoundsVideoFromRes(): Single<List<SoundVideoRes>> {
        return Single.fromCallable {
            val videoArray = context.resources.getStringArray(R.array.sound_video)
            val videoMap = mutableListOf<SoundVideoRes>()
            videoArray.forEach {
                val split = it.split("::")
                val transcription = split.first()
                val soundType: SoundType = when (split[2]) {
                    "consonantSounds" -> SoundType.CONSONANT_SOUND
                    "rControlledVowels" -> SoundType.R_CONTROLLED_VOWELS
                    "vowelSounds" -> SoundType.VOWEL_SOUNDS
                    else -> throw IllegalArgumentException()
                }
                videoMap.add(SoundVideoRes(transcription, split[1], soundType, split[3]))
            }
            videoMap
        }
    }

    private fun getContrastingSoundsVideoFromRes(): Single<List<ContrastingSoundVideoRes>> {
        return Single.fromCallable {
            val videoArray = context.resources.getStringArray(R.array.sound_contrasting_video)
            val videoList = mutableListOf<ContrastingSoundVideoRes>()
            videoArray.forEach {
                val split = it.split("::")
                videoList.add(ContrastingSoundVideoRes(split[0], split[1], split[2], split[3]))
            }
            videoList
        }
    }

    private fun getMostCommonWordsVideoFromRes(): Single<List<MostCommonWordsVideoRes>> {
        return Single.fromCallable {
            val videoArray = context.resources.getStringArray(R.array.most_common_words_video)
            val videoList = mutableListOf<MostCommonWordsVideoRes>()
            videoArray.forEach {
                val split = it.split("::")
                videoList.add(MostCommonWordsVideoRes(split[0], split[1]))
            }
            videoList
        }
    }

    private fun getAdvancedExercisesVideoFromRes(): Single<List<AdvancedExercisesVideoRes>> {
        return Single.fromCallable {
            val videoArray = context.resources.getStringArray(R.array.advanced_exercises_video)
            val videoList = mutableListOf<AdvancedExercisesVideoRes>()
            videoArray.forEach {
                val split = it.split("::")
                videoList.add(
                    AdvancedExercisesVideoRes(
                        split[0],
                        split[1],
                        split.getOrNull(2),
                        split.getOrNull(3)
                    )
                )
            }
            videoList
        }
    }
}