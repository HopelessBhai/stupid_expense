package com.example.stupidexpense.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extension-backed DataStore instance scoped to the application context.
private val Context.totalDataStore by preferencesDataStore(name = "stupid_expense_prefs")

/**
 * Repository that owns the persistence logic for the running total.
 * Hides DataStore specifics from the rest of the app so the ViewModel remains testable.
 */
class TotalRepository(appContext: Context) {

    private val applicationContext = appContext.applicationContext
    private val totalKey = floatPreferencesKey("total_amount")

    /** Stream of saved totals; emits the default (0f) when no value exists yet. */
    val totalStream: Flow<Float> = applicationContext.totalDataStore.data.map { prefs ->
        prefs[totalKey] ?: 0f
    }

    /** Persists the provided amount so reopening the app restores the value. */
    suspend fun saveTotal(amount: Float) {
        applicationContext.totalDataStore.edit { prefs ->
            prefs[totalKey] = amount
        }
    }

    /**
     * Convenience helper that increments the persisted total by the provided delta.
     * Eliminates the need for callers to read the stream, wait for the value, and
     * then write it back just to perform a simple addition.
     */
    suspend fun addToTotal(delta: Float) {
        applicationContext.totalDataStore.edit { prefs ->
            val current = prefs[totalKey] ?: 0f
            prefs[totalKey] = current + delta
        }
    }

    /** Helper dedicated to the reset action. */
    suspend fun resetTotal() = saveTotal(0f)
}
