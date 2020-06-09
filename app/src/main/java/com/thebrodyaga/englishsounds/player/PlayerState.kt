package com.thebrodyaga.englishsounds.player


data class PlayerState constructor(
    var window: Int = 0,
    var position: Long = 0,
    var whenReady: Boolean = true
)