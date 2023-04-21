package com.thebrodyaga.infrastructure.preferences.impl

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.thebrodyaga.core.utils.coroutines.AppScope
import com.thebrodyaga.infrastructure.preferences.api.PrefBool
import com.thebrodyaga.infrastructure.preferences.api.PrefInt
import com.thebrodyaga.infrastructure.preferences.api.PrefString
import com.thebrodyaga.infrastructure.preferences.api.PrefValue
import com.thebrodyaga.infrastructure.preferences.api.Preferences
import com.thebrodyaga.infrastructure.preferences.api.PreferencesPair
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import java.io.File
import androidx.datastore.preferences.core.Preferences as DatastorePreferences

class PreferencesStorage(
    preferencesFile: File,
    private val appScope: AppScope,
) : Preferences.Storage {


    private val dataStore = PreferenceDataStoreFactory.create { preferencesFile }

    override suspend fun put(pair: PreferencesPair, value: PrefValue) {
        dataStore.updateData {
            val (key, _) = pair
            val preferences = it.toMutablePreferences()
            when (value) {
                is PrefBool -> preferences[booleanPreferencesKey(key)] = value.value
                is PrefInt -> preferences[intPreferencesKey(key)] = value.value
                is PrefString -> preferences[stringPreferencesKey(key)] = value.value
            }
            preferences
        }
    }

    override fun putNonBlock(pair: PreferencesPair, value: PrefValue) {
        appScope.launch { put(pair, value) }
    }

    override fun <V : PrefValue> get(pair: PreferencesPair): Flow<V> {
        return dataStore.data.map {
            val (key, default) = pair
            val dataStoreKey = default.toDataStoreKey(key)
            val dataStoreValue = it[dataStoreKey]
            default.updateOrOld(dataStoreValue) as? V ?: throw ClassCastException()
        }
    }

    override fun remove(pair: PreferencesPair) {
        Json.encodeToString(TEEST(1))
        dataStore.data.map {
            val (key, default) = pair
            val dataStoreKey = default.toDataStoreKey(key)
            it.toMutablePreferences().remove(dataStoreKey)
        }
    }

    override fun clear() {

        dataStore.data.map {
            it.toMutablePreferences().clear()
        }
    }

    private fun PrefValue.updateOrOld(newValue: Any?): PrefValue = when (this) {
        is PrefBool -> PrefBool(newValue as? Boolean ?: value)
        is PrefInt -> PrefInt(newValue as? Int ?: value)
        is PrefString -> PrefString(newValue as? String ?: value)
    }

    private fun PrefValue.toDataStoreKey(key: String): DatastorePreferences.Key<out Any> = when (this) {
        is PrefBool -> booleanPreferencesKey(key)
        is PrefInt -> intPreferencesKey(key)
        is PrefString -> stringPreferencesKey(key)
    }
}

@Serializable
data class TEEST(
    val id: Int,
)
