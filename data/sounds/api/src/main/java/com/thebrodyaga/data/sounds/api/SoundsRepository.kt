package com.thebrodyaga.data.sounds.api

import com.thebrodyaga.data.sounds.api.model.AmericanSoundDto
import com.thebrodyaga.data.sounds.api.model.PracticeWordDto
import io.reactivex.Observable
import kotlinx.coroutines.flow.Flow

interface SoundsRepository {

    fun getAllSounds(fromDb: Boolean = false): Observable<List<AmericanSoundDto>>
    fun getSounds(transcription: String): Observable<AmericanSoundDto>
    fun tryCopySounds(): Observable<List<AmericanSoundDto>>
    fun getAllPracticeWords(fromDb: Boolean = false): Observable<List<PracticeWordDto>>
}