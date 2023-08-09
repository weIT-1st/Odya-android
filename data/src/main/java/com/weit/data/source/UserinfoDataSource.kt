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

class UserinfoDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    val Context.userInfoDataStore: DataStore<Preferences> by preferencesDataStore(
        name = "com.weit.odya.username"
    )

    suspend fun setUsername(username: String) {
        context.userInfoDataStore.edit { preference ->
            preference[KEY_USERNAME] = username
        }
    }

    suspend fun getUsername(): String? {
        val flow = context.userInfoDataStore.data
            .catch{exception ->
                if (exception is IOException){
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[KEY_USERNAME]
            }
        val value = flow.firstOrNull()
        return value
    }

    suspend fun setNickname(nickcame: String){
        context.userInfoDataStore.edit {preference ->
            preference[KEY_NICKNAME] = nickcame
        }
    }

    suspend fun getNickname(): String?{
        val flow = context.userInfoDataStore.data
            .catch { exception ->
                if(exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preference ->
                preference[KEY_NICKNAME]
            }
        val value = flow.firstOrNull()
        return value
    }

    suspend fun setBirth(){

    }

    suspend fun getBirth(){

    }

    suspend fun getGender(){

    }

    suspend fun setGender(){

    }

    private companion object{
        val KEY_USERNAME = stringPreferencesKey(name = "username")
        val KEY_NICKNAME = stringPreferencesKey(name = "nickname")
        val KEY_BIRTH = stringPreferencesKey(name = "birth")
        val KEY_GENDER = stringPreferencesKey(name = "gender")
    }
}