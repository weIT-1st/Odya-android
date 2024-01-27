package com.weit.data.source

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.time.LocalDate
import javax.inject.Inject

class UserInfoDataSource @Inject constructor(
    private val context: Context,
) {
    val Context.userInfoDataStore: DataStore<Preferences> by preferencesDataStore(
        name = "com.weit.odya.userInfo",
    )

    private companion object {
        val KEY_USERNAME = stringPreferencesKey(name = "username")
        val KEY_NICKNAME = stringPreferencesKey(name = "nickname")
        val KEY_BIRTH = stringPreferencesKey(name = "birth")
        val KEY_GENDER = stringPreferencesKey(name = "gender")
        val KEY_TERM_ID_LIST = stringSetPreferencesKey(name = "termidlist")
        val KEY_USER_ID = longPreferencesKey(name = "userId")
        val KEY_IS_ALARM_ALL = booleanPreferencesKey(name = "isAlarmAll")
        val KEY_IS_ALARM_LIKE = booleanPreferencesKey(name = "isAlarmLike")
        val KEY_IS_ALARM_COMMENT = booleanPreferencesKey(name = "isAlarmComment")
        val KEY_IS_ALARM_MARKETING = booleanPreferencesKey(name = "isAlarmMarketing")
        val KEY_IS_LOCATION_INFO = booleanPreferencesKey(name = "isLocationInfo")
    }

    suspend fun setUserId(userId: Long) {
        context.userInfoDataStore.edit { preference ->
            preference[KEY_USER_ID] = userId
        }
    }

    suspend fun getUserId(): Long? {
        val flow = context.userInfoDataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[KEY_USER_ID]
            }
        return flow.firstOrNull()
    }

    suspend fun setUsername(username: String) {
        context.userInfoDataStore.edit { preference ->
            preference[KEY_USERNAME] = username
        }
    }

    suspend fun getUsername(): String? {
        val flow = context.userInfoDataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[KEY_USERNAME]
            }
        return flow.firstOrNull()
    }

    suspend fun setNickname(nickcame: String) {
        context.userInfoDataStore.edit { preference ->
            preference[KEY_NICKNAME] = nickcame
        }
    }

    suspend fun getNickname(): String? {
        val flow = context.userInfoDataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preference ->
                preference[KEY_NICKNAME]
            }
        return flow.firstOrNull()
    }

    suspend fun setBirth(birth: LocalDate) {
        context.userInfoDataStore.edit { preference ->
            preference[KEY_BIRTH] = birth.toString()
        }
    }

    suspend fun getBirth(): LocalDate? {
        val flow = context.userInfoDataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[KEY_BIRTH]
            }
        return LocalDate.parse(flow.firstOrNull())
    }

    suspend fun setGender(gender: String) {
        context.userInfoDataStore.edit { preference ->
            preference[KEY_GENDER] = gender
        }
    }

    suspend fun getGender(): String? {
        val flow = context.userInfoDataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preference ->
                preference[KEY_GENDER]
            }
        return flow.firstOrNull()
    }

    suspend fun setTermIdList(termIdList: Set<String>) {
        context.userInfoDataStore.edit { preference ->
            preference[KEY_TERM_ID_LIST] = termIdList
        }
    }

    suspend fun getTermIdList(): Set<String>? {
        val flow = context.userInfoDataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preference ->
                preference[KEY_TERM_ID_LIST]
            }
        return flow.firstOrNull()
    }

    suspend fun setIsAlarmAll(isAlarmAll : Boolean) {
        context.userInfoDataStore.edit { preference ->
            preference[KEY_IS_ALARM_ALL] = isAlarmAll
        }
    }

    suspend fun getIsAlarmAll() : Boolean? {
        val flow = context.userInfoDataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preference ->
                preference[KEY_IS_ALARM_ALL]
            }
        return flow.firstOrNull()
    }

    suspend fun setIsAlarmLike(isAlarmLike : Boolean) {
        context.userInfoDataStore.edit { preference ->
            preference[KEY_IS_ALARM_LIKE] = isAlarmLike
        }
    }

    suspend fun getIsAlarmLike() : Boolean? {
        val flow = context.userInfoDataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preference ->
                preference[KEY_IS_ALARM_LIKE]
            }
        return flow.firstOrNull()
    }

    suspend fun setIsAlarmComment(isAlarmComment : Boolean) {
        context.userInfoDataStore.edit { preference ->
            preference[KEY_IS_ALARM_COMMENT] = isAlarmComment
        }
    }

    suspend fun getIsAlarmComment() : Boolean? {
        val flow = context.userInfoDataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preference ->
                preference[KEY_IS_ALARM_COMMENT]
            }
        return flow.firstOrNull()
    }

    suspend fun setIsAlarmMarketing(isAlarmMarketing : Boolean) {
        context.userInfoDataStore.edit { preference ->
            preference[KEY_IS_ALARM_MARKETING] = isAlarmMarketing
        }
    }

    suspend fun getIsAlarmMarketing() : Boolean? {
        val flow = context.userInfoDataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preference ->
                preference[KEY_IS_ALARM_MARKETING]
            }
        return flow.firstOrNull()
    }

    suspend fun setIsLocationInfo(isLocationInfo : Boolean) {
        context.userInfoDataStore.edit { preference ->
            preference[KEY_IS_LOCATION_INFO] = isLocationInfo
        }
    }

    suspend fun getIsLocationInfo() : Boolean? {
        val flow = context.userInfoDataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preference ->
                preference[KEY_IS_LOCATION_INFO]
            }
        return flow.firstOrNull()
    }
}
