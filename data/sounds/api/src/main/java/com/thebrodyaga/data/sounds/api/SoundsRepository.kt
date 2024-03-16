package com.thebrodyaga.data.sounds.api

import com.thebrodyaga.data.sounds.api.model.AmericanSoundDto
import com.thebrodyaga.data.sounds.api.model.PracticeWordDto
import kotlinx.coroutines.flow.Flow

interface SoundsRepository {

    val isWasExistSoundsInInternalStorage: Boolean
    fun getAllSounds(): Flow<List<AmericanSoundDto>>
    fun getSounds(transcription: String): Flow<AmericanSoundDto>
    fun getAllPracticeWords(): Flow<List<PracticeWordDto>>
}