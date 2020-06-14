package com.thebrodyaga.englishsounds.repository.impl

import android.content.Context
import com.thebrodyaga.englishsounds.R
import com.thebrodyaga.englishsounds.domine.entities.resources.AdvancedExercisesVideoRes
import com.thebrodyaga.englishsounds.domine.entities.resources.ContrastingSoundVideoRes
import com.thebrodyaga.englishsounds.domine.entities.resources.MostCommonWordsVideoRes
import com.thebrodyaga.englishsounds.domine.entities.resources.SoundVideoRes
import com.thebrodyaga.englishsounds.repository.SoundsVideoRepository
import io.reactivex.Single

class SoundsVideoRepositoryImpl constructor(
    val context: Context
) : SoundsVideoRepository {

    override fun getSoundsVideo(): Single<Map<String, SoundVideoRes>> =
        getSoundsVideoFromRes()

    override fun getContrastingSoundsVideo(): Single<List<ContrastingSoundVideoRes>> =
        getContrastingSoundsVideoFromRes()

    override fun getMostCommonWordsVideo(): Single<List<MostCommonWordsVideoRes>> =
        getMostCommonWordsVideoFromRes()

    override fun getAdvancedExercisesVideo(): Single<List<AdvancedExercisesVideoRes>> =
        getAdvancedExercisesVideoFromRes()

    private fun getSoundsVideoFromRes(): Single<Map<String, SoundVideoRes>> {
        val videoArray = context.resources.getStringArray(R.array.sound_video)
        val videoMap = mutableMapOf<String, SoundVideoRes>()
        videoArray.forEach {
            val split = it.split("::")
            val transcription = split.first()
            videoMap[transcription] = SoundVideoRes(transcription, split[1])
        }
        return Single.just(videoMap)
    }

    private fun getContrastingSoundsVideoFromRes(): Single<List<ContrastingSoundVideoRes>> {
        val videoArray = context.resources.getStringArray(R.array.sound_contrasting_video)
        val videoList = mutableListOf<ContrastingSoundVideoRes>()
        videoArray.forEach {
            val split = it.split("::")
            videoList.add(ContrastingSoundVideoRes(split[0], split[1], split[2], split[3]))
        }
        return Single.just(videoList)
    }

    private fun getMostCommonWordsVideoFromRes(): Single<List<MostCommonWordsVideoRes>> {
        val videoArray = context.resources.getStringArray(R.array.most_common_words_video)
        val videoList = mutableListOf<MostCommonWordsVideoRes>()
        videoArray.forEach {
            val split = it.split("::")
            videoList.add(MostCommonWordsVideoRes(split[0], split[1]))
        }
        return Single.just(videoList)
    }

    private fun getAdvancedExercisesVideoFromRes(): Single<List<AdvancedExercisesVideoRes>> {
        val videoArray = context.resources.getStringArray(R.array.advanced_exercises_video)
        val videoList = mutableListOf<AdvancedExercisesVideoRes>()
        videoArray.forEach {
            val split = it.split("::")
            videoList.add(AdvancedExercisesVideoRes(split[0], split[1]))
        }
        return Single.just(videoList)
    }
}