package com.thebrodyaga.feature.soundDetails.impl.ui.mapper

import android.content.Context
import androidx.core.text.HtmlCompat
import com.thebrodyaga.ad.api.AppAd
import com.thebrodyaga.ad.api.GoogleAdUiModel
import com.thebrodyaga.brandbook.component.data.DataUiModel
import com.thebrodyaga.brandbook.component.data.left.DataLeftUiModel
import com.thebrodyaga.brandbook.component.data.right.DataRightUiModel
import com.thebrodyaga.brandbook.component.play.PlayButtonBindingState
import com.thebrodyaga.brandbook.component.play.PlayButtonUiModel
import com.thebrodyaga.brandbook.model.UiModel
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

class SoundDetailsMapper @Inject constructor() {

    fun mapFullList(context: Context, sound: AmericanSoundDto, playerState: AudioPlayerState, ad: AppAd): List<UiModel> {
        val playingFile = (playerState as? AudioPlayerState.Playing)?.audioFile
        return mapDetails(context, sound, playingFile, ad)
            .plus(mapWordList(context, sound, playingFile))
    }

    private fun mapWordList(context: Context, sound: AmericanSoundDto, playingFile: File?): List<UiModel> = buildList {
        val spellingWordList = sound.spellingWordList
        if (spellingWordList.isNotEmpty()) {
            add(getHeader(WordsHeader.Type.SPELLING))
            addAll(spellingWordList.map { it.toUiModel(context, playingFile) })
        }
        val beginningSound = sound.soundPracticeWords.beginningSound
        if (beginningSound.isNotEmpty()) {
            add(getHeader(WordsHeader.Type.BEGINNING_SOUND))
            addAll(beginningSound.map { it.toUiModel(context, playingFile) })
        }
        val middleSound = sound.soundPracticeWords.middleSound
        if (middleSound.isNotEmpty()) {
            add(getHeader(WordsHeader.Type.MIDDLE_SOUND))
            addAll(middleSound.map { it.toUiModel(context, playingFile) })
        }
        val endSound = sound.soundPracticeWords.endSound
        if (endSound.isNotEmpty()) {
            add(getHeader(WordsHeader.Type.END_SOUND))
            addAll(endSound.map { it.toUiModel(context, playingFile) })
        }
    }

    private fun mapDetails(context: Context, sound: AmericanSoundDto, playingFile: File?, ad: AppAd) = buildList {
        val videoAndDescription = context.getVideoAndDescription()
        val (videoMap, descriptionMap) = videoAndDescription

        add(SoundDetailsImageUiModel(sound.photoPath))
        val soundName = sound.name.plus(" ").plus("[${sound.transcription}]")
        add(
            DataUiModel(
                leftSide = dataLeftText(soundName),
                rightSide = playIcon(isSameAudio(context, playingFile, sound.audioPath)),
                payload = audioPayload(context, sound.audioPath),
            )
        )
        when (ad) {
            AppAd.Empty, AppAd.Loading -> Unit
            is AppAd.Google -> add(GoogleAdUiModel(ad.ad, false))
        }
        val description = descriptionMap[sound.transcription] ?: ""
        add(SoundDetailsDescriptionUiModel(description))
        val videoUrl = videoMap[sound.transcription]
        if (!videoUrl.isNullOrEmpty()) add(SoundDetailsVideoUiModel(videoUrl))
    }

    private fun SpellingWordDto.toUiModel(context: Context, playingFile: File?): UiModel {
        return DataUiModel(
            leftSide = dataLeftText(HtmlCompat.fromHtml(transcription, HtmlCompat.FROM_HTML_MODE_LEGACY)),
            rightSide = playIcon(isSameAudio(context, playingFile, audioPath)),
            payload = audioPayload(context, audioPath),
        )
    }

    private fun PracticeWordDto.toUiModel(context: Context, playingFile: File?): UiModel {
        return DataUiModel(
            leftSide = dataLeftText(name),
            rightSide = playIcon(isSameAudio(context, playingFile, audioPath)),
            payload = audioPayload(context, audioPath),
        )
    }

    private fun audioPayload(context: Context, audioPath: String): File {
        return File(context.filesDir, audioPath)
    }

    private fun isSameAudio(context: Context, playingFile: File?, audioPath: String): Boolean {
        playingFile ?: return false
        val audioFile = File(context.filesDir, audioPath)
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

    private fun playIcon(isPlaying: Boolean) = DataRightUiModel.PlayIcon(
        playIcon = PlayButtonUiModel(
            state = if (isPlaying) PlayButtonBindingState.PlayToPause() else PlayButtonBindingState.PauseToPlay(),
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
