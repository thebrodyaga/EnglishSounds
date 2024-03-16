package com.thebrodyaga.data.sounds.impl

import android.app.Application
import android.content.Context
import com.google.gson.Gson
import com.thebrodyaga.core.utils.zip.ZipUtils
import com.thebrodyaga.data.setting.api.SettingManager
import com.thebrodyaga.data.sounds.api.SoundsRepository
import com.thebrodyaga.data.sounds.api.model.AmericanSoundDto
import com.thebrodyaga.data.sounds.api.model.PracticeWordDto
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onSubscription
import timber.log.Timber
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.util.Collections
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

private const val AMERICAN_SOUNDS_ZIP_VERSION = 1

class AmericanSoundsRepositoryImpl @Inject constructor(
    application: Application,
    private val gson: Gson,
    private val settingManager: SettingManager,
) : SoundsRepository {

    private val sourceDir = File(application.filesDir, americanSounds)
    private val sourceDirExists: Boolean = sourceDir.listFiles()?.isNotEmpty() == true

    private val context: Context = application
    private var loadingSounds: AtomicBoolean = AtomicBoolean(false)
    private val soundsFlow = MutableSharedFlow<List<AmericanSoundDto>>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    private val practiceWordsFlow = soundsFlow
        .onSubscription { checkIsNeedToLoadSounds() }
        .map { updatePracticeWords(it) }

    override val isWasExistSoundsInInternalStorage: Boolean = sourceDirExists

    override fun getAllSounds(): Flow<List<AmericanSoundDto>> = soundsFlow
        .onSubscription { checkIsNeedToLoadSounds() }

    override fun getAllPracticeWords(): Flow<List<PracticeWordDto>> = practiceWordsFlow

    override fun getSounds(transcription: String): Flow<AmericanSoundDto> {
        return getAllSounds()
            .map { it.first { soundDao -> soundDao.transcription == transcription } }
    }

    private fun checkIsNeedToLoadSounds() {
        if (!isSoundsLoaded() && !isSoundsLoading())
            loadSounds()
    }

    private fun isSoundsLoaded() =
        soundsFlow.replayCache.isNotEmpty() && soundsFlow.replayCache.first().isNotEmpty()

    private fun isSoundsLoading() = loadingSounds.get()

    private fun loadSounds() {
        loadingSounds.set(true)
        try {
            val list = soundsFromAssets()
            soundsFlow.tryEmit(list)
        } catch (e: Throwable) {
            Timber.e(e, "Failing of load sounds from res")
            throw e
        } finally {
            loadingSounds.set(false)
        }
    }

    private fun soundsFromAssets(): MutableList<AmericanSoundDto> {
        val synchronizedList =
            Collections.synchronizedList(mutableListOf<AmericanSoundDto>())
        val service = Executors.newCachedThreadPool()
        val lastVersionCode = settingManager.getLastVersionCode()
        if (lastVersionCode < AMERICAN_SOUNDS_ZIP_VERSION && sourceDirExists) {
            Timber.i("delete americanSoundsZip in internal because app new version")
            ZipUtils.delete(sourceDir)
        }

        if (!sourceDirExists) {
            Timber.i("copy americanSoundsZip")
            val outputStream = File(context.filesDir, americanSoundsZip).outputStream()
            context.assets.open(americanSoundsZip).use { assets -> assets.copyTo(outputStream) }
            val zipInInternal = File(context.filesDir, americanSoundsZip)
            Timber.i("unzip americanSoundsZip")
            ZipUtils.unzip(zipInInternal, context.filesDir)
            Timber.i("delete americanSoundsZip in internal ")
            ZipUtils.delete(zipInInternal)
            settingManager.setLastVersionCode(AMERICAN_SOUNDS_ZIP_VERSION)
        } else {
            Timber.i("americanSoundsZip exists in internal storage")
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
        return synchronizedList
    }

    private fun updatePracticeWords(sounds: List<AmericanSoundDto>): MutableList<PracticeWordDto> {
        val result = mutableListOf<PracticeWordDto>()
        sounds.forEach { sound ->
            result.addAll(sound.soundPracticeWords.beginningSound)
            result.addAll(sound.soundPracticeWords.middleSound)
            result.addAll(sound.soundPracticeWords.endSound)
        }
        return result
    }

    companion object {
        private const val jsonPath = "json"
        private const val americanSoundsZip = "AmericanSounds.zip"
        private const val americanSounds = "AmericanSounds"
    }
}
