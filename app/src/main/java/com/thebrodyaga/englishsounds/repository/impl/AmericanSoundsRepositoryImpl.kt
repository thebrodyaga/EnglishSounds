package com.thebrodyaga.englishsounds.repository.impl

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.thebrodyaga.englishsounds.BuildConfig
import com.thebrodyaga.englishsounds.domine.entities.data.AmericanSoundDto
import com.thebrodyaga.englishsounds.domine.entities.data.PracticeWordDto
import com.thebrodyaga.englishsounds.repository.SoundsRepository
import com.thebrodyaga.englishsounds.tools.SettingManager
import com.thebrodyaga.englishsounds.utils.UnzipFile
import com.thebrodyaga.englishsounds.utils.ZipUtils
import io.reactivex.Observable
import io.reactivex.Single
import timber.log.Timber
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class AmericanSoundsRepositoryImpl constructor(
    private val context: Context,
    private val gson: Gson,
    private val settingManager: SettingManager
) : SoundsRepository {
    override fun getSounds(transcription: String): Observable<AmericanSoundDto> {
        return getAllSounds()
            .map { it.first { soundDao -> soundDao.transcription == transcription } }
    }

    private var soundsCash = listOf<AmericanSoundDto>()
    private var practiceWords = listOf<PracticeWordDto>()

    override fun tryCopySounds(): Observable<List<AmericanSoundDto>> {
        return when {
            soundsCash.isEmpty() || practiceWords.isEmpty()
            -> soundsFromAssets().toObservable()
            else -> Observable.fromCallable { soundsCash }
        }
    }

    override fun getAllSounds(fromDb: Boolean): Observable<List<AmericanSoundDto>> {
        return if (fromDb || this.soundsCash.isEmpty())
            tryCopySounds()
        else Observable.fromCallable { this.soundsCash }
    }

    override fun getAllPracticeWords(fromDb: Boolean): Observable<List<PracticeWordDto>> {
        return if (fromDb || this.practiceWords.isEmpty())
            tryCopySounds().map { practiceWords }
        else Observable.fromCallable { this.practiceWords }
    }

    private fun soundsFromAssets(): Single<List<AmericanSoundDto>> {
        return Single.create<List<AmericanSoundDto>> {
            try {
                val synchronizedList =
                    Collections.synchronizedList(mutableListOf<AmericanSoundDto>())
                val service = Executors.newCachedThreadPool()
                val sourceDir = File(context.filesDir, americanSounds)
                val lastVersionCode = settingManager.getLastVersionCode()
                if (lastVersionCode < BuildConfig.VERSION_CODE && sourceDir.exists()) {
                    Timber.i("delete americanSoundsZip in internal because app new version")
                    ZipUtils.delete(sourceDir)
                }

                if (!sourceDir.exists()) {
                    Timber.i("copy americanSoundsZip")
                    UnzipFile.copyFile(context, americanSoundsZip)
                    val zipInInternal = File(context.filesDir, americanSoundsZip)
                    Timber.i("unzip americanSoundsZip")
                    ZipUtils.unzip(zipInInternal, context.filesDir)
                    Timber.i("delete americanSoundsZip in internal ")
                    ZipUtils.delete(zipInInternal)
                    settingManager.setLastVersionCode(BuildConfig.VERSION_CODE)
                }
                val jsonDir: Array<File> = File(sourceDir, jsonPath).listFiles()
                    ?: throw IOException("где жисоны?")

                jsonDir.forEach { soundFilePath ->
                    service.submit {
                        val sound =
                            gson.fromJson(FileReader(soundFilePath), AmericanSoundDto::class.java)
                        synchronizedList.add(sound)
                    }
                }
                service.shutdown()
                service.awaitTermination(1, TimeUnit.MINUTES)
                it.onSuccess(synchronizedList)
            } catch (e: Throwable) {
                it.tryOnError(e)
            }
        }.doOnSuccess {
            soundsCash = it.sortedBy { sound -> sound.transcription }
            updatePracticeWords(it)
        }
    }

    private fun updatePracticeWords(sounds: List<AmericanSoundDto>) {
        val result = mutableListOf<PracticeWordDto>()
        sounds.forEach { sound ->
            result.addAll(sound.soundPracticeWords.beginningSound)
            result.addAll(sound.soundPracticeWords.middleSound)
            result.addAll(sound.soundPracticeWords.endSound)
        }
        practiceWords = result
    }


    companion object {
        private const val jsonPath = "json"
        private const val americanSoundsZip = "AmericanSounds.zip"
        private const val americanSounds = "AmericanSounds"
    }
}
