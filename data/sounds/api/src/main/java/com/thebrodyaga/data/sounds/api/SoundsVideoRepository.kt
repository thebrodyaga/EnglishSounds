package com.thebrodyaga.data.sounds.api

import com.thebrodyaga.data.sounds.api.model.AdvancedExercisesVideoRes
import com.thebrodyaga.data.sounds.api.model.ContrastingSoundVideoRes
import com.thebrodyaga.data.sounds.api.model.MostCommonWordsVideoRes
import com.thebrodyaga.data.sounds.api.model.SoundVideoRes
import kotlinx.coroutines.flow.Flow

interface SoundsVideoRepository {

    fun getSoundsVideo(): Flow<List<SoundVideoRes>>
    fun getContrastingSoundsVideo(): Flow<List<ContrastingSoundVideoRes>>
    fun getMostCommonWordsVideo(): Flow<List<MostCommonWordsVideoRes>>
    fun getAdvancedExercisesVideo(): Flow<List<AdvancedExercisesVideoRes>>
}