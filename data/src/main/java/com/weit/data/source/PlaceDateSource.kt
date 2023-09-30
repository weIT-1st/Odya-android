package com.weit.data.source

import android.graphics.Bitmap
import android.util.Log
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.Place.Field
import com.google.android.libraries.places.api.model.PlaceTypes
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.FetchPhotoResponse
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.weit.data.BuildConfig
import com.weit.data.model.map.GeocodingResult
import com.weit.data.service.PlaceService
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
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
    )

    suspend fun getPlaceDetail(placeId: String): GeocodingResult =
        service.getPlaceDetail(BuildConfig.GOOGLE_MAP_KEY, placeId)

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
            }.addOnFailureListener { exception: Exception ->
                trySend(emptyList())
            }
        awaitClose { }
    }

    suspend fun getPlaceImage(placeId: String): Flow<Bitmap?> = callbackFlow {
        val fields = listOf(Field.PHOTO_METADATAS)
        val placeRequest = FetchPlaceRequest.newInstance(placeId, fields)

        placesClient.fetchPlace(placeRequest)
            .addOnSuccessListener { response: FetchPlaceResponse ->
                val place = response.place
                val metadata = place.photoMetadatas

                // 이거 구글 문서에 있으서 작성은 했는데 이해가 안되요.. 이거 왜써요?
                if (metadata == null || metadata.isEmpty()) {
                    return@addOnSuccessListener
                }

                val photoMetadata = metadata.first()
                val photoRequest = FetchPhotoRequest.builder(photoMetadata)
                    .build()

                placesClient.fetchPhoto(photoRequest)
                    .addOnSuccessListener { fetchPhotoResponse: FetchPhotoResponse ->
                        Log.d("getPlaceImage", "fetchplace success but fetchphoto success")
                        trySend(fetchPhotoResponse.bitmap)
                    }.addOnFailureListener {
                        Log.d("getPlaceImage", "fetchplace success but fetchphoto faile : " + it.message)
                        trySend(null)
                    }
            }.addOnFailureListener {
                Log.d("getPlaceImage", "fetchplace fail : " + it.message)
                trySend(null)
            }
        awaitClose { }
    }
}
