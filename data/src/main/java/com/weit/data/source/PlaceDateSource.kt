package com.weit.data.source

import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.PlaceTypes
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.orhanobut.logger.Logger
import com.weit.data.BuildConfig
import com.weit.data.model.map.GeocodingResult
import com.weit.data.model.map.PlaceDetailResponse
import com.weit.data.service.PlaceService
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import java.util.Locale
import javax.inject.Inject

class PlaceDateSource @Inject constructor(
    private val service: PlaceService,
    private val sessionToken: AutocompleteSessionToken,
    private val placesClient: PlacesClient,
) {

    // TODO 너무 많아서 쓰다 말았는데 ESTABLISHMENT가 안붙은 업체가 있는지 검증 필요
    private val defaultResultTypes = listOf(
        PlaceTypes.ESTABLISHMENT,
        PlaceTypes.POINT_OF_INTEREST,
    )

    private val defaultPlaceField = listOf(
        Place.Field.NAME,
        Place.Field.ID,
        Place.Field.ADDRESS,
        Place.Field.LAT_LNG,
    )

    private val detailPlaceField = listOf(
        Place.Field.NAME,
        Place.Field.ID,
        Place.Field.ADDRESS,
        Place.Field.ADDRESS_COMPONENTS,
        Place.Field.LAT_LNG,
        Place.Field.PHOTO_METADATAS,
    )

    suspend fun getPlaceDetail(
        placeId: String,
        language: String = "ko",
    ): PlaceDetailResponse {
        val result = service.getPlaceDetail(
            BuildConfig.GOOGLE_MAP_KEY,
            placeId,
            language,
        )
        Logger.t("MainTest").i("$result")
        return result
    }

    suspend fun getPlaceDetailWithFields(
        placeId: String,
        language: String = "ko",
        fields: List<String>,
    ): PlaceDetailResponse {
        val result = service.getPlaceDetailWithFields(
            BuildConfig.GOOGLE_MAP_KEY,
            placeId,
            language,
            fields.joinToString(","),
        )
        Logger.t("MainTest").i("$result")
        return result
    }

    suspend fun getPlace(placeId: String): Place? = callbackFlow {
        val request = FetchPlaceRequest.builder(placeId, defaultPlaceField).build()
        placesClient.fetchPlace(request).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                trySend(task.result.place)
            } else {
                trySend(null)
            }
        }
        awaitClose { }
    }.first()

    suspend fun getPlacesByCoordinate(
        latitude: Double,
        longitude: Double,
        language: String = "ko",
        resultTypes: List<String> = defaultResultTypes,
    ): GeocodingResult {
        return service.getPlacesByCoordinate(
            apiKey = BuildConfig.GOOGLE_MAP_KEY,
            latlng = "$latitude,$longitude",
            lang = language,
            resultType = resultTypes.joinToString("|"),
        )
    }

    suspend fun searchPlaces(
        query: String,
        language: String = "ko",
        types: List<String> = defaultResultTypes,
    ): List<AutocompletePrediction> = callbackFlow<List<AutocompletePrediction>> {
        val newRequest = FindAutocompletePredictionsRequest.builder()
            .setCountries(Locale.getDefault().country)
            .setTypesFilter(listOf(PlaceTypes.ESTABLISHMENT))
            .setSessionToken(sessionToken)
            .setQuery(query)
            .build()

        placesClient.findAutocompletePredictions(newRequest)
            .addOnSuccessListener { response ->
                trySend(response.autocompletePredictions)
            }.addOnFailureListener { exception: Exception ->
                trySend(emptyList())
            }
        awaitClose { }
    }.first()
}
