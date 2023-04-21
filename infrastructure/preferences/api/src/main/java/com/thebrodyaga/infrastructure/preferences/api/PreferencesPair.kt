package com.thebrodyaga.infrastructure.preferences.api

infix fun String.to(that: PrefValue) = PreferencesPair(this, that)

data class PreferencesPair(
    val key: String,
    val default: PrefValue,
)

sealed interface PrefValue

data class PrefInt(
    val value: Int,
) : PrefValue

data class PrefString(
    val value: String,
) : PrefValue

data class PrefBool(
    val value: Boolean,
) : PrefValue