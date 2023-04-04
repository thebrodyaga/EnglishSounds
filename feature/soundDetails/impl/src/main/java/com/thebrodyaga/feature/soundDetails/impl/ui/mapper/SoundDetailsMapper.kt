package com.thebrodyaga.feature.soundDetails.impl.ui.mapper

import android.app.Application
import android.content.Context
import androidx.core.text.HtmlCompat
import com.thebrodyaga.brandbook.component.data.DataUiModel
import com.thebrodyaga.brandbook.component.data.left.DataLeftUiModel
import com.thebrodyaga.brandbook.component.data.right.DataRightUiModel
import com.thebrodyaga.brandbook.model.UiModel
import com.thebrodyaga.core.uiUtils.image.IconContainer
import com.thebrodyaga.core.uiUtils.image.ImageViewUiModel
import com.thebrodyaga.core.uiUtils.text.TextContainer
import com.thebrodyaga.core.uiUtils.text.TextViewUiModel
import com.thebrodyaga.data.sounds.api.model.AmericanSoundDto
import com.thebrodyaga.data.sounds.api.model.PracticeWordDto
import com.thebrodyaga.data.sounds.api.model.SpellingWordDto
import com.thebrodyaga.feature.audioPlayer.api.AudioPlayerState
import com.thebrodyaga.feature.soundDetails.impl.R
import com.thebrodyaga.feature.soundDetails.impl.ui.adapter.SoundDetailsDescriptionUiModel
import com.thebrodyaga.feature.soundDetails.impl.ui.adapter.SoundDetailsImageUiModel
import com.thebrodyaga.feature.soundDetails.impl.ui.adapter.SoundDetailsVideoUiModel
import com.thebrodyaga.legacy.WordsHeader
import java.io.File
import javax.inject.Inject

class SoundDetailsMapper @Inject constructor(
    private val application: Application,
) {

    fun mapFullList(sound: AmericanSoundDto, playerState: AudioPlayerState): List<UiModel> {
        val playingFile = (playerState as? AudioPlayerState.Playing)?.audioFile
        return mapDetails(sound, playingFile)
            .plus(mapWordList(sound, playingFile))
    }

    private fun mapWordList(sound: AmericanSoundDto, playingFile: File?): List<UiModel> = buildList {
        val spellingWordList = sound.spellingWordList
        if (spellingWordList.isNotEmpty()) {
            add(getHeader(WordsHeader.Type.SPELLING))
            addAll(spellingWordList.map { it.toUiModel(playingFile) })
        }
        val beginningSound = sound.soundPracticeWords.beginningSound
        if (beginningSound.isNotEmpty()) {
            add(getHeader(WordsHeader.Type.BEGINNING_SOUND))
            addAll(beginningSound.map { it.toUiModel(playingFile) })
        }
        val middleSound = sound.soundPracticeWords.middleSound
        if (middleSound.isNotEmpty()) {
            add(getHeader(WordsHeader.Type.MIDDLE_SOUND))
            addAll(middleSound.map { it.toUiModel(playingFile) })
        }
        val endSound = sound.soundPracticeWords.endSound
        if (endSound.isNotEmpty()) {
            add(getHeader(WordsHeader.Type.END_SOUND))
            addAll(endSound.map { it.toUiModel(playingFile) })
        }
    }

    private fun mapDetails(sound: AmericanSoundDto, playingFile: File?) = buildList {
        val videoAndDescription = application.getVideoAndDescription()
        val (videoMap, descriptionMap) = videoAndDescription

        add(SoundDetailsImageUiModel(sound.photoPath))
        val soundName = sound.name.plus(" ").plus("[${sound.transcription}]")
        add(
            DataUiModel(
                leftSide = dataLeftText(soundName),
                rightSide = playIcon(isSameAudio(playingFile, sound.audioPath)),
                payload = audioPayload(sound.audioPath),
            )
        )
        val description = descriptionMap[sound.transcription] ?: ""
        add(SoundDetailsDescriptionUiModel(description))
        val videoUrl = videoMap[sound.transcription]
        if (!videoUrl.isNullOrEmpty()) add(SoundDetailsVideoUiModel(videoUrl))
    }

    private fun SpellingWordDto.toUiModel(playingFile: File?): UiModel {
        return DataUiModel(
            leftSide = dataLeftText(HtmlCompat.fromHtml(transcription, HtmlCompat.FROM_HTML_MODE_LEGACY)),
            rightSide = playIcon(isSameAudio(playingFile, audioPath)),
            payload = audioPayload(audioPath),
        )
    }

    private fun PracticeWordDto.toUiModel(playingFile: File?): UiModel {
        return DataUiModel(
            leftSide = dataLeftText(name),
            rightSide = playIcon(isSameAudio(playingFile, audioPath)),
            payload = audioPayload(audioPath),
        )
    }

    private fun audioPayload(audioPath: String): File {
        return File(application.filesDir, audioPath)
    }

    private fun isSameAudio(playingFile: File?, audioPath: String): Boolean {
        playingFile ?: return false
        val audioFile = File(application.filesDir, audioPath)
        return playingFile.path == audioFile.path
    }

    private fun dataLeftText(
        text: CharSequence,
        textAppearance: Int = R.attr.textAppearanceBodyLarge,
    ) = DataLeftUiModel.TwoLineText(
        firstLineText = TextViewUiModel.Raw(
            text = TextContainer.Raw(text),
            textAppearance = textAppearance,
        )
    )

    private fun playIcon(isPlay: Boolean = true) = DataRightUiModel.TextWithIcon(
        icon = ImageViewUiModel(
            icon = IconContainer.Res(if (isPlay) R.drawable.ic_pause else R.drawable.ic_play),
            iconTint = R.attr.colorSecondary
        )
    )

    private fun getHeader(soundType: WordsHeader.Type): DataUiModel = DataUiModel(
        leftSide = DataLeftUiModel.TwoLineText(
            firstLineText = TextViewUiModel.Raw(
                text = TextContainer.Res(soundType.humanName()),
                textAppearance = R.attr.textAppearanceHeadlineLarge,
                maxLines = 2,
            )
        )
    )

    private fun Context.getVideoAndDescription(): Pair<MutableMap<String, String>, MutableMap<String, String>> {
        val videoArray = resources.getStringArray(R.array.sound_video)
        val soundArray = resources.getStringArray(R.array.sound_description)
        val videoMap = mutableMapOf<String, String>()
        val descriptionMap = mutableMapOf<String, String>()
        videoArray.forEach {
            val split = it.split("::")
            videoMap[split.first()] = split[1]
        }
        soundArray.forEach {
            val split = it.split("::")
            descriptionMap[split.first()] = split[1]
        }
        return Pair(videoMap, descriptionMap)
    }
}
