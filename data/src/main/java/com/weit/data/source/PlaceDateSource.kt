package com.weit.data.source

import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.PlaceTypes
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.weit.data.BuildConfig
import com.weit.data.model.map.GeocodingResult
import com.weit.data.service.PlaceService
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class PlaceDateSource @Inject constructor(
    private val service: PlaceService,
    private val sessionToken: AutocompleteSessionToken,
    private val placesClient: PlacesClient,
) {
    suspend fun getPlaceDetail(placeId: String): GeocodingResult =
        service.getPlaceDetail(BuildConfig.GOOGLE_MAP_KEY, placeId)

    suspend fun searchPlaces(query: String): Flow<List<AutocompletePrediction>> = callbackFlow {
        val newRequest = FindAutocompletePredictionsRequest.builder()
            .setCountries("KR")
            .setTypesFilter(listOf(PlaceTypes.ESTABLISHMENT))
            .setSessionToken(sessionToken)
            .setQuery(query)
            .build()

        placesClient.findAutocompletePredictions(newRequest)
            .addOnSuccessListener { response ->
                trySend(response.autocompletePredictions)
            }.addOnFailureListener { exception: Exception? ->
                trySend(emptyList())
            }
        awaitClose { }
    }
}
