package com.thebrodyaga.data.sounds.api

import com.thebrodyaga.data.sounds.api.model.AdvancedExercisesVideoRes
import com.thebrodyaga.data.sounds.api.model.ContrastingSoundVideoRes
import com.thebrodyaga.data.sounds.api.model.MostCommonWordsVideoRes
import com.thebrodyaga.data.sounds.api.model.SoundVideoRes
import io.reactivex.Single

interface SoundsVideoRepository {

    fun getSoundsVideo(): Single<List<SoundVideoRes>>
    fun getContrastingSoundsVideo(): Single<List<ContrastingSoundVideoRes>>
    fun getMostCommonWordsVideo(): Single<List<MostCommonWordsVideoRes>>
    fun getAdvancedExercisesVideo(): Single<List<AdvancedExercisesVideoRes>>
}