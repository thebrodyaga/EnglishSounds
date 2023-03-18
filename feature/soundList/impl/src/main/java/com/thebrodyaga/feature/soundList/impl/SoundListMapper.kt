package com.thebrodyaga.feature.soundList.impl

import com.thebrodyaga.brandbook.component.data.DataUiModel
import com.thebrodyaga.brandbook.component.data.left.DataLeftUiModel
import com.thebrodyaga.brandbook.component.sound.SoundCardUiModel
import com.thebrodyaga.brandbook.model.UiModel
import com.thebrodyaga.brandbook.utils.text.TextContainer
import com.thebrodyaga.brandbook.utils.text.TextViewUiModel
import com.thebrodyaga.data.sounds.api.model.AmericanSoundDto
import com.thebrodyaga.data.sounds.api.model.SoundType
import com.thebrodyaga.legacy.humanName
import javax.inject.Inject

class SoundListMapper @Inject constructor() {

    fun map(soundsMap: Map<String, AmericanSoundDto>): List<UiModel> = buildList {
        val sounds = soundsMap.values.sortedBy { it.transcription }
        val vowelSounds = mutableListOf<SoundCardUiModel>()
        val rControlledVowels = mutableListOf<SoundCardUiModel>()
        val consonantSounds = mutableListOf<SoundCardUiModel>()
        sounds.forEach { soundDto ->
            val word = soundDto.spellingWordList
                .filter { it.name.length <= 6 }
                .let {
                    if (it.isNotEmpty()) it.random()
                    else soundDto.spellingWordList.minBy { word -> word.name.length }
                }
            val soundUiModel = SoundCardUiModel(
                transcription = TextViewUiModel.Raw(text = TextContainer.Raw(soundDto.transcription)),
                word = TextViewUiModel.Raw(text = TextContainer.Raw(word.name)),
                transcriptionTint = R.color.colorPrimary,
                payload = soundDto
            )
            when (soundDto.soundType) {
                SoundType.CONSONANT_SOUND -> consonantSounds.add(soundUiModel)
                SoundType.R_CONTROLLED_VOWELS -> rControlledVowels.add(soundUiModel)
                SoundType.VOWEL_SOUNDS -> vowelSounds.add(soundUiModel)
            }
        }
        if (vowelSounds.isNotEmpty()) {
            add(getHeader(SoundType.VOWEL_SOUNDS))
            addAll(vowelSounds)
        }
        if (rControlledVowels.isNotEmpty()) {
            add(getHeader(SoundType.R_CONTROLLED_VOWELS))
            addAll(rControlledVowels)
        }
        if (consonantSounds.isNotEmpty()) {
            add(getHeader(SoundType.CONSONANT_SOUND))
            addAll(consonantSounds)
        }
    }

    private fun getHeader(soundType: SoundType): DataUiModel = DataUiModel(
        leftSide = DataLeftUiModel.TwoLineText(
            firstLineText = TextViewUiModel.Raw(
                text = TextContainer.Res(soundType.humanName()),
                textAppearance = R.attr.textAppearanceHeadline4,
                maxLines = 2,
            )
        )
    )
}