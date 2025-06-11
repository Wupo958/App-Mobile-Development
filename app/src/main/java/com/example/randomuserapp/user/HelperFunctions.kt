package com.example.randomuserapp.user

import java.text.SimpleDateFormat
import java.util.Locale
import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.randomuserapp.user.SortOption
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "settings")

enum class SortOption(val displayName: String) {
    FIRST_NAME("First Name"),
    LAST_NAME("Last Name"),
    DOB("Birthday")
}


object SortPreferences {
    private val SORT_KEY = stringPreferencesKey("sort_option")

    fun getSortOption(context: Context): Flow<SortOption> =
        context.dataStore.data.map { prefs ->
            SortOption.values().find { it.name == prefs[SORT_KEY] } ?: SortOption.FIRST_NAME
        }

    suspend fun setSortOption(context: Context, option: SortOption) {
        context.dataStore.edit { prefs ->
            prefs[SORT_KEY] = option.name
        }
    }
}

fun formatDate(isoDate: String): String {
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val date = parser.parse(isoDate)
        formatter.format(date!!)
    } catch (e: Exception) {
        isoDate // Fallback
    }
}