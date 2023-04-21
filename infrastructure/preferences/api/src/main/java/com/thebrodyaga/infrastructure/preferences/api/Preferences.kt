package com.thebrodyaga.infrastructure.preferences.api

import kotlinx.coroutines.flow.Flow

sealed interface Preferences {

    interface Storage : Preferences

    suspend fun put(pair: PreferencesPair, value: PrefValue)
    fun putNonBlock(pair: PreferencesPair, value: PrefValue)

    fun <V : PrefValue> get(pair: PreferencesPair): Flow<V>
    fun remove(pair: PreferencesPair)
    fun clear()
}

class AppPreferences(
    private val delegate: Preferences.Storage,
) : Preferences by delegate

class UserPreferences(
    private val delegate: Preferences.Storage,
) : Preferences by delegate
