package com.thebrodyaga.englishsounds.repository

import com.thebrodyaga.englishsounds.domine.entities.data.AmericanSoundDto
import com.thebrodyaga.englishsounds.domine.entities.data.PracticeWordDto
import com.thebrodyaga.englishsounds.domine.entities.data.SoundDto
import io.reactivex.Observable

interface SoundsRepository {

    fun getAllSounds(fromDb: Boolean = false): Observable<List<AmericanSoundDto>>
    fun getSounds(transcription: String): Observable<AmericanSoundDto>
    fun tryCopySounds(): Observable<List<AmericanSoundDto>>
    fun getAllPracticeWords(fromDb: Boolean = false): Observable<List<PracticeWordDto>>
}