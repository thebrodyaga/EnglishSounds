package com.thebrodyaga.data.sounds.impl

import android.content.Context
import com.google.gson.Gson
import com.thebrodyaga.core.utils.zip.ZipUtils
import com.thebrodyaga.data.sounds.api.SoundsRepository
import com.thebrodyaga.data.sounds.api.model.AmericanSoundDto
import com.thebrodyaga.data.sounds.api.model.PracticeWordDto
import com.thebrodyaga.feature.setting.api.SettingManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.util.Collections
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val AMERICAN_SOUNDS_ZIP_VERSION = 1

class AmericanSoundsRepositoryImpl @Inject constructor(
    private val context: Context,
    private val gson: Gson,
    private val settingManager: SettingManager
) : SoundsRepository {
    override fun getSounds(transcription: String): Flow<AmericanSoundDto> {
        return getAllSounds()
            .map { it.first { soundDao -> soundDao.transcription == transcription } }
    }

    private var soundsCash = listOf<AmericanSoundDto>()
    private var practiceWords = listOf<PracticeWordDto>()

    override fun tryCopySounds(): Flow<List<AmericanSoundDto>> {
        return when {
            soundsCash.isEmpty() || practiceWords.isEmpty() -> soundsFromAssets()
            else -> flow { emit(soundsCash) }
        }
    }

    override fun getAllSounds(fromDb: Boolean): Flow<List<AmericanSoundDto>> {
        return if (fromDb || this.soundsCash.isEmpty())
            tryCopySounds()
        else flow { emit(this@AmericanSoundsRepositoryImpl.soundsCash) }
    }

    override fun getAllPracticeWords(fromDb: Boolean): Flow<List<PracticeWordDto>> {
        return if (fromDb || this.practiceWords.isEmpty())
            tryCopySounds().map { practiceWords }
        else flow { emit(this@AmericanSoundsRepositoryImpl.practiceWords) }
    }

    private fun soundsFromAssets(): Flow<List<AmericanSoundDto>> {
        return flow {
            val synchronizedList =
                Collections.synchronizedList(mutableListOf<AmericanSoundDto>())
            val service = Executors.newCachedThreadPool()
            val sourceDir = File(context.filesDir, americanSounds)
            val lastVersionCode = settingManager.getLastVersionCode()
            if (lastVersionCode < AMERICAN_SOUNDS_ZIP_VERSION && sourceDir.exists()) {
                Timber.i("delete americanSoundsZip in internal because app new version")
                ZipUtils.delete(sourceDir)
            }

            if (!sourceDir.exists()) {
                Timber.i("copy americanSoundsZip")
                val outputStream = File(context.filesDir, americanSoundsZip).outputStream()
                context.assets.open(americanSoundsZip).use { assets -> assets.copyTo(outputStream) }
                val zipInInternal = File(context.filesDir, americanSoundsZip)
                Timber.i("unzip americanSoundsZip")
                ZipUtils.unzip(zipInInternal, context.filesDir)
                Timber.i("delete americanSoundsZip in internal ")
                ZipUtils.delete(zipInInternal)
                settingManager.setLastVersionCode(AMERICAN_SOUNDS_ZIP_VERSION)
            }
            val jsonDir: Array<File> = File(sourceDir, jsonPath).listFiles()
                ?: throw IOException("где жисоны?")

            jsonDir.forEach { soundFilePath ->
                service.submit {
                    val sound = gson.fromJson(FileReader(soundFilePath), AmericanSoundDto::class.java)
                    synchronizedList.add(sound)
                }
            }
            service.shutdown()
            service.awaitTermination(1, TimeUnit.MINUTES)
            emit(synchronizedList)
        }.onEach {
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
