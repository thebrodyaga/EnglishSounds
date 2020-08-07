package com.thebrodyaga.englishsounds.domine.entities.data

data class AppRateDto constructor(
    val soundShowingCount: Int = 0
) {
    fun needShowRateRequest(): Boolean = when {
        soundShowingCount >= SOUND_SHOW_COUNT_BEFORE_RATE -> true
        else -> false
    }

    companion object {
        const val SOUND_SHOW_COUNT_BEFORE_RATE = 3
    }
}