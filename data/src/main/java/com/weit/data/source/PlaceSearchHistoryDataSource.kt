package com.weit.data.source

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.weit.data.service.PlaceSearchHistoryService
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import okio.IOException
import retrofit2.Response
import javax.inject.Inject

class PlaceSearchHistoryDataSource @Inject constructor(
    private val context: Context,
    private val service: PlaceSearchHistoryService
) {
    val Context.placeSearchHistoryDataStore: DataStore<Preferences> by preferencesDataStore(
        name = "com.weit.odya.placeSearchHistory"
    )

    private companion object{
        val KEY_RECENT_PLACE_SEARCH = stringSetPreferencesKey(name = "search_place")
    }

    suspend fun setRecentPlaceSearch(searchTermList: Set<String>){
        context.placeSearchHistoryDataStore.edit { preference ->
            preference[KEY_RECENT_PLACE_SEARCH] = searchTermList
        }
    }

    suspend fun getRecentPlaceSearch(): Set<String>?{
        val flow = context.placeSearchHistoryDataStore.data
            .catch { exception ->
                if (exception is IOException){
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preference ->
                preference[KEY_RECENT_PLACE_SEARCH]
            }
        return flow.firstOrNull()
    }
    suspend fun registerPlaceSearchHistory(
        searchTerm: String
    ): Response<Unit> =
        service.registerPlaceSearchHistory(searchTerm)

    suspend fun getPlaceSearchHistoryRank(): List<String> =
        service.getPlaceSearchHistoryRank()

    suspend fun getPlaceSearchHistoryAgeRank(
        ageRange: Int?
    ): List<String> =
        service.getPlaceSearchHistoryAgeRank(ageRange)
}
