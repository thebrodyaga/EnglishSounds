package com.thebrodyaga.englishsounds.repository

import com.thebrodyaga.englishsounds.domine.entities.resources.ContrastingSoundVideoRes
import com.thebrodyaga.englishsounds.domine.entities.resources.SoundVideoRes
import io.reactivex.Single

interface SoundsVideoRepository {

    fun getSoundsVideo(): Single<Map<String, SoundVideoRes>>
    fun getContrastingSoundsVideo(): Single<Set<ContrastingSoundVideoRes>>
}