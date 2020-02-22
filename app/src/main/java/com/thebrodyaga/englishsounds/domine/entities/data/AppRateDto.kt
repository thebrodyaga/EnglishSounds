package com.thebrodyaga.englishsounds.domine.entities.data

data class AppRateDto(
    val soundShowingCount: Int = 0,
    val lessFourRateCount: Int = 0,
    val rated: Boolean = false
) {
    fun needShowRateRequest(): Boolean = when {
        rated -> false
        lessFourRateCount < MAX_RATE_TRY -> when {
            lessFourRateCount <= 0 && soundShowingCount >= SOUND_SHOW_COUNT_BEFORE_RATE -> true
            lessFourRateCount == 1 && soundShowingCount >= SOUND_SHOW_COUNT_BEFORE_RATE_SECOND_TRY -> true
            else -> false
        }
        else -> false
    }

    companion object {
        const val SOUND_SHOW_COUNT_BEFORE_RATE = 4
        const val SOUND_SHOW_COUNT_BEFORE_RATE_SECOND_TRY = 8
        const val MAX_RATE_TRY = 2
    }
}