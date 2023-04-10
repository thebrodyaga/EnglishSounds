package com.thebrodyaga.feature.videoList.impl.carousel

import androidx.annotation.AttrRes
import com.thebrodyaga.brandbook.component.data.DataUiModel
import com.thebrodyaga.brandbook.component.data.left.DataLeftUiModel
import com.thebrodyaga.brandbook.component.data.right.DataRightUiModel
import com.thebrodyaga.brandbook.component.sound.mini.SoundCardMiniUiModel
import com.thebrodyaga.core.uiUtils.drawable.DrawableUiModel
import com.thebrodyaga.core.uiUtils.drawable.shapeDrawable
import com.thebrodyaga.core.uiUtils.shape.shapeCircle
import com.thebrodyaga.core.uiUtils.text.TextContainer
import com.thebrodyaga.core.uiUtils.text.TextViewUiModel
import com.thebrodyaga.data.sounds.api.model.AmericanSoundDto
import com.thebrodyaga.data.sounds.api.model.SoundType
import com.thebrodyaga.feature.videoList.impl.R
import com.thebrodyaga.legacy.*
import javax.inject.Inject

class VideoCarouselMapper @Inject constructor() {

    fun mapUi(list: List<VideoListItem>) = buildList {
        list.forEachIndexed { index, videoItems ->
            add(getHeader(videoItems))
            add(
                VideoCarouselUiModel(videoItems.list
                    .map { mapVideoItemUiModel(it) })
            )
        }
    }

    fun mapVideoItemUiModel(video: VideoItem): VideoCarouselItemUiModel {
        val (firstSound, secondSound) = video.getSoundModel()
        return VideoCarouselItemUiModel(
            videoId = video.videoId,
            title = video.title,
            firstSound = firstSound,
            secondSound = secondSound,
        )
    }

    private fun getHeader(videoList: VideoListItem): DataUiModel = DataUiModel(
        leftSide = DataLeftUiModel.TwoLineText(
            firstLineText = TextViewUiModel.Raw(
                text = TextContainer.Res(videoList.title),
                textAppearance = R.attr.textAppearanceHeadlineSmall,
                maxLines = 2
            )
        ),
        rightSide = DataRightUiModel.Button.Text(
            text = TextViewUiModel.Raw(
                text = TextContainer.Res(R.string.show_all),
            )
        ),
        payload = videoList
    )

    private fun VideoItem.getSoundModel(): Pair<SoundCardMiniUiModel?, SoundCardMiniUiModel?> {
        return when (this) {
            is AdvancedExercisesVideoItem -> firstTranscription?.getSoundModel() to secondTranscription?.getSoundModel()
            is ContrastingSoundVideoItem -> firstTranscription?.getSoundModel() to secondTranscription?.getSoundModel()
            is MostCommonWordsVideoItem -> null to null
            is SoundVideoItem -> sound.getSoundModel() to null
        }
    }

    private fun AmericanSoundDto?.getSoundModel(): SoundCardMiniUiModel? {
        this ?: return null
        val transcriptionTint = when (soundType) {
            SoundType.CONSONANT_SOUND -> R.attr.staticColorConsonantSounds
            SoundType.R_CONTROLLED_VOWELS -> R.attr.staticColorRControlledVowelsSounds
            SoundType.VOWEL_SOUNDS -> R.attr.staticColorVowelSounds
        }
        return SoundCardMiniUiModel(
            transcription = TextViewUiModel.Raw(text = TextContainer.Raw(transcription)),
            transcriptionBg = getTranscriptionBg(transcriptionTint),
            payload = this,
        )
    }

    private fun getTranscriptionBg(@AttrRes transcriptionTint: Int): DrawableUiModel = DrawableUiModel(
        drawable = shapeDrawable(shapeCircle()),
        tint = transcriptionTint
    )
}