package com.thebrodyaga.englishsounds.domine.entities.data

import com.google.gson.annotations.SerializedName
import com.thebrodyaga.englishsounds.domine.entities.ui.SoundsDetailsListItem

interface WordDto {
    val sound: String
    val name: String
    val audioPath: String
}

data class SpellingWordDto constructor(
    override val sound: String,
    override val name: String,
    override val audioPath: String,
    var transcription: String
) : WordDto,SoundsDetailsListItem

data class PracticeWordDto constructor(
    override val sound: String,
    override val name: String,
    override val audioPath: String,
    val soundPositionType: SoundPositionType
) : WordDto, SoundsDetailsListItem

enum class SoundPositionType {
    @SerializedName("beginningSound")
    BEGINNING_SOUND,
    @SerializedName("middleSound")
    MIDDLE_SOUND,
    @SerializedName("endSound")
    END_SOUND
}