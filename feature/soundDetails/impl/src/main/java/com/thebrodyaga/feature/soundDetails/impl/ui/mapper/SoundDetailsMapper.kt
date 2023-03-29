package com.thebrodyaga.feature.soundDetails.impl.ui.mapper

import android.app.Application
import androidx.core.text.HtmlCompat
import com.thebrodyaga.brandbook.component.data.DataUiModel
import com.thebrodyaga.brandbook.component.data.left.DataLeftUiModel
import com.thebrodyaga.brandbook.component.data.right.DataRightUiModel
import com.thebrodyaga.brandbook.model.UiModel
import com.thebrodyaga.brandbook.utils.image.IconContainer
import com.thebrodyaga.brandbook.utils.image.ImageViewUiModel
import com.thebrodyaga.brandbook.utils.text.TextContainer
import com.thebrodyaga.brandbook.utils.text.TextViewUiModel
import com.thebrodyaga.data.sounds.api.model.AmericanSoundDto
import com.thebrodyaga.data.sounds.api.model.PracticeWordDto
import com.thebrodyaga.data.sounds.api.model.SpellingWordDto
import com.thebrodyaga.feature.soundDetails.impl.R
import com.thebrodyaga.feature.soundDetails.impl.ui.adapter.SoundDetailsDescriptionUiModel
import com.thebrodyaga.feature.soundDetails.impl.ui.adapter.SoundDetailsImageUiModel
import com.thebrodyaga.feature.soundDetails.impl.ui.adapter.SoundDetailsVideoUiModel
import com.thebrodyaga.feature.soundDetails.impl.ui.getVideoAndDescription
import com.thebrodyaga.legacy.WordsHeader
import javax.inject.Inject

class SoundDetailsMapper @Inject constructor(
    private val application: Application,
) {

    fun mapFullList(sound: AmericanSoundDto): List<UiModel> {
        return mapDetails(sound)
            .plus(mapWordList(sound))
    }

    private fun mapWordList(sound: AmericanSoundDto): List<UiModel> = buildList {
        val spellingWordList = sound.spellingWordList
        if (spellingWordList.isNotEmpty()) {
            add(getHeader(WordsHeader.Type.SPELLING))
//            result.add(ShortAdItem(AdTag.SOUND_DETAILS))
            addAll(spellingWordList.map { it.toUiModel() })
        }
        val beginningSound = sound.soundPracticeWords.beginningSound
        if (beginningSound.isNotEmpty()) {
            add(getHeader(WordsHeader.Type.BEGINNING_SOUND))
//            result.add(ShortAdItem(AdTag.SOUND_DETAILS_SECOND))
            addAll(beginningSound.map { it.toUiModel() })
        }
        val middleSound = sound.soundPracticeWords.middleSound
        if (middleSound.isNotEmpty()) {
            add(getHeader(WordsHeader.Type.MIDDLE_SOUND))
//            result.add(ShortAdItem(AdTag.SOUND_DETAILS_THIRD))
            addAll(middleSound.map { it.toUiModel() })
        }
        val endSound = sound.soundPracticeWords.endSound
        if (endSound.isNotEmpty()) {
            add(getHeader(WordsHeader.Type.END_SOUND))
//            result.add(ShortAdItem(AdTag.SOUND_DETAILS_FOURTH))
            addAll(endSound.map { it.toUiModel() })
        }
    }

    private fun mapDetails(sound: AmericanSoundDto) = buildList {
        val videoAndDescription = application.getVideoAndDescription()
        val (videoMap, descriptionMap) = videoAndDescription

        add(SoundDetailsImageUiModel(sound.photoPath))
        val soundName = sound.name.plus(" ").plus("[${sound.transcription}]")
        add(
            DataUiModel(
                leftSide = dataLeftText(soundName),
                rightSide = playIcon()
            )
        )
        val description = descriptionMap[sound.transcription] ?: ""
        add(SoundDetailsDescriptionUiModel(description))
        val videoUrl = videoMap[sound.transcription]
        if (!videoUrl.isNullOrEmpty()) add(SoundDetailsVideoUiModel(videoUrl))
    }

    private fun SpellingWordDto.toUiModel(): UiModel {
        return DataUiModel(
            leftSide = dataLeftText(HtmlCompat.fromHtml(transcription, HtmlCompat.FROM_HTML_MODE_LEGACY)),
            rightSide = playIcon()
        )
    }

    private fun PracticeWordDto.toUiModel(): UiModel {
        return DataUiModel(
            leftSide = dataLeftText(name),
            rightSide = playIcon()
        )
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
            icon = IconContainer.Res(R.drawable.ic_play),
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
}
