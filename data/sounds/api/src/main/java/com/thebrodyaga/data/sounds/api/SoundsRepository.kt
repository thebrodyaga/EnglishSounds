package com.thebrodyaga.data.sounds.api

import com.thebrodyaga.data.sounds.api.model.AmericanSoundDto
import com.thebrodyaga.data.sounds.api.model.PracticeWordDto
import kotlinx.coroutines.flow.Flow

interface SoundsRepository {

    fun getAllSounds(fromDb: Boolean = false): Flow<List<AmericanSoundDto>>
    fun getSounds(transcription: String): Flow<AmericanSoundDto>
    fun tryCopySounds(): Flow<List<AmericanSoundDto>>
    fun getAllPracticeWords(fromDb: Boolean = false): Flow<List<PracticeWordDto>>
}