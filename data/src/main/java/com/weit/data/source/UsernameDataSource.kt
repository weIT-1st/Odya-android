package com.weit.data.source

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class UsernameDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    val Context.userNameDataStore: DataStore<Preferences> by preferencesDataStore(
        name = "com.weit.odya.username"
    )

    suspend fun setUsername(username: String) {
        context.userNameDataStore.edit { preference ->
            preference[KEY_NAME] = username
        }
    }

    suspend fun getUsername(): String? {
        val flow = context.userNameDataStore.data
            .catch{exception ->
                if (exception is IOException){
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[KEY_NAME]
            }
        val value = flow.firstOrNull()
        return value
    }

    private companion object{
        val KEY_NAME = stringPreferencesKey(
            name = "name"
        )
    }
}