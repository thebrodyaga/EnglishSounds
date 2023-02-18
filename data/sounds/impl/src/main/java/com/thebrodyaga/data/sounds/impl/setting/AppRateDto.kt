package com.thebrodyaga.data.sounds.impl.setting

data class AppRateDto constructor(
    val soundShowingCount: Int = 0
) {
    fun needShowRateRequest(firstAppStart: Boolean): Boolean = when {
        firstAppStart && soundShowingCount >= 3 -> true
        !firstAppStart && soundShowingCount >= 2 -> true
        else -> false
    }
}