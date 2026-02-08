package com.example.liliplayer.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.liliplayer.domain.model.RepeatMode
import com.example.liliplayer.domain.model.SortOrder
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val SORT_ORDER = stringPreferencesKey("sort_order")
        val SHUFFLE_ENABLED = booleanPreferencesKey("shuffle_enabled")
        val REPEAT_MODE = stringPreferencesKey("repeat_mode")
        val LAST_PLAYED_SONG_ID = longPreferencesKey("last_played_song_id")
        val LAST_POSITION = longPreferencesKey("last_position")
    }

    val sortOrder: Flow<SortOrder> = context.dataStore.data.map { prefs ->
        try {
            SortOrder.valueOf(prefs[Keys.SORT_ORDER] ?: SortOrder.TITLE_ASC.name)
        } catch (e: IllegalArgumentException) {
            SortOrder.TITLE_ASC
        }
    }

    val shuffleEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.SHUFFLE_ENABLED] ?: false
    }

    val repeatMode: Flow<RepeatMode> = context.dataStore.data.map { prefs ->
        try {
            RepeatMode.valueOf(prefs[Keys.REPEAT_MODE] ?: RepeatMode.OFF.name)
        } catch (e: IllegalArgumentException) {
            RepeatMode.OFF
        }
    }

    val lastPlayedSongId: Flow<Long> = context.dataStore.data.map { prefs ->
        prefs[Keys.LAST_PLAYED_SONG_ID] ?: -1L
    }

    suspend fun setSortOrder(order: SortOrder) {
        context.dataStore.edit { it[Keys.SORT_ORDER] = order.name }
    }

    suspend fun setShuffleEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.SHUFFLE_ENABLED] = enabled }
    }

    suspend fun setRepeatMode(mode: RepeatMode) {
        context.dataStore.edit { it[Keys.REPEAT_MODE] = mode.name }
    }

    suspend fun setLastPlayed(songId: Long, position: Long) {
        context.dataStore.edit {
            it[Keys.LAST_PLAYED_SONG_ID] = songId
            it[Keys.LAST_POSITION] = position
        }
    }
}
