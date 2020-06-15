package com.thebrodyaga.englishsounds.repository

import com.thebrodyaga.englishsounds.domine.entities.resources.AdvancedExercisesVideoRes
import com.thebrodyaga.englishsounds.domine.entities.resources.ContrastingSoundVideoRes
import com.thebrodyaga.englishsounds.domine.entities.resources.MostCommonWordsVideoRes
import com.thebrodyaga.englishsounds.domine.entities.resources.SoundVideoRes
import io.reactivex.Single

interface SoundsVideoRepository {

    fun getSoundsVideo(): Single<List<SoundVideoRes>>
    fun getContrastingSoundsVideo(): Single<List<ContrastingSoundVideoRes>>
    fun getMostCommonWordsVideo(): Single<List<MostCommonWordsVideoRes>>
    fun getAdvancedExercisesVideo(): Single<List<AdvancedExercisesVideoRes>>
}