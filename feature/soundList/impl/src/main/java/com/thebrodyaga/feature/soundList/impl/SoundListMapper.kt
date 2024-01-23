package com.thebrodyaga.feature.soundList.impl

import androidx.annotation.AttrRes
import com.thebrodyaga.ad.api.AdLoadingSmallUiModel
import com.thebrodyaga.ad.api.AppAd
import com.thebrodyaga.ad.api.GoogleAdUiModel
import com.thebrodyaga.brandbook.component.data.DataUiModel
import com.thebrodyaga.brandbook.component.data.left.DataLeftUiModel
import com.thebrodyaga.brandbook.component.sound.SoundCardUiModel
import com.thebrodyaga.brandbook.model.UiModel
import com.thebrodyaga.core.uiUtils.drawable.DrawableUiModel
import com.thebrodyaga.core.uiUtils.drawable.shapeDrawable
import com.thebrodyaga.core.uiUtils.shape.shapeCircle
import com.thebrodyaga.core.uiUtils.text.TextContainer
import com.thebrodyaga.core.uiUtils.text.TextViewUiModel
import com.thebrodyaga.data.sounds.api.model.AmericanSoundDto
import com.thebrodyaga.data.sounds.api.model.SoundType
import com.thebrodyaga.data.sounds.api.model.SpellingWordDto
import com.thebrodyaga.legacy.humanName
import javax.inject.Inject

class SoundListMapper @Inject constructor() {

    private val wordsCache = mutableMapOf<String, SpellingWordDto>()

    fun map(soundsMap: Map<String, AmericanSoundDto>, firstAd: AppAd, secondAd: AppAd): List<UiModel> =
        buildList {
            val sounds = soundsMap.values.sortedBy { it.transcription }
            val vowelSounds = mutableListOf<SoundCardUiModel>()
            val rControlledVowels = mutableListOf<SoundCardUiModel>()
            val consonantSounds = mutableListOf<SoundCardUiModel>()
            sounds.forEach { soundDto ->
                val word = wordsCache.getOrPut(soundDto.transcription) {
                    soundDto.spellingWordList
                        .filter { it.name.length <= 6 }
                        .let {
                            if (it.isNotEmpty()) it.random()
                            else soundDto.spellingWordList.minBy { word -> word.name.length }
                        }
                }
                val transcriptionTint = when (soundDto.soundType) {
                    SoundType.CONSONANT_SOUND -> R.attr.staticColorConsonantSounds
                    SoundType.R_CONTROLLED_VOWELS -> R.attr.staticColorRControlledVowelsSounds
                    SoundType.VOWEL_SOUNDS -> R.attr.staticColorVowelSounds
                }
                val soundUiModel = SoundCardUiModel(
                    transcription = TextViewUiModel.Raw(text = TextContainer.Raw(soundDto.transcription)),
                    transcriptionBg = getTranscriptionBg(transcriptionTint),
                    word = TextViewUiModel.Raw(text = TextContainer.Raw(word.name)),
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
            addAd(firstAd)
            if (rControlledVowels.isNotEmpty()) {
                add(getHeader(SoundType.R_CONTROLLED_VOWELS))
                addAll(rControlledVowels)
            }
            if (consonantSounds.isNotEmpty()) {
                add(getHeader(SoundType.CONSONANT_SOUND))
                addAll(consonantSounds)
            }
        }

    private fun MutableList<UiModel>.addAd(ad: AppAd) {
        when (ad) {
            AppAd.Empty, AppAd.Loading -> Unit
            is AppAd.Google -> add(GoogleAdUiModel(ad.ad, false))
        }
    }

    private fun getTranscriptionBg(@AttrRes transcriptionTint: Int): DrawableUiModel = DrawableUiModel(
        drawable = shapeDrawable(shapeCircle()),
        tint = transcriptionTint
    )

    private fun getHeader(soundType: SoundType): DataUiModel = DataUiModel(
        payload = soundType,
        leftSide = DataLeftUiModel.TwoLineText(
            firstLineText = TextViewUiModel.Raw(
                text = TextContainer.Res(soundType.humanName()),
                textAppearance = R.attr.textAppearanceHeadlineLarge,
                maxLines = 2,
            )
        )
    )
}