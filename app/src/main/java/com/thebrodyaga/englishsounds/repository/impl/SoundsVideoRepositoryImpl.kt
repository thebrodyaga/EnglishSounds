package com.thebrodyaga.englishsounds.repository.impl

import android.content.Context
import com.thebrodyaga.englishsounds.R
import com.thebrodyaga.englishsounds.domine.entities.resources.ContrastingSoundVideoRes
import com.thebrodyaga.englishsounds.domine.entities.resources.SoundVideoRes
import com.thebrodyaga.englishsounds.repository.SoundsVideoRepository
import io.reactivex.Single

class SoundsVideoRepositoryImpl constructor(
    val context: Context
) : SoundsVideoRepository {

    override fun getSoundsVideo(): Single<Map<String, SoundVideoRes>> =
        getSoundsVideoFromRes()

    override fun getContrastingSoundsVideo(): Single<Set<ContrastingSoundVideoRes>> =
        getContrastingSoundsVideoFromRes()

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

    private fun getContrastingSoundsVideoFromRes(): Single<Set<ContrastingSoundVideoRes>> {
        val videoArray = context.resources.getStringArray(R.array.sound_contrasting_video)
        val videoSet = mutableSetOf<ContrastingSoundVideoRes>()
        videoArray.forEach {
            val split = it.split("::")
            videoSet.add(ContrastingSoundVideoRes(split[0], split[1], split[2], split[3]))
        }
        return Single.just(videoSet)
    }
}