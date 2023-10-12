package com.weit.data.source

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.LocationServices
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
import com.gun0912.tedpermission.TedPermissionResult
import com.gun0912.tedpermission.coroutine.TedPermission
import com.weit.data.BuildConfig
import com.weit.data.model.map.GeocodingResult
import com.weit.data.service.PlaceService
import com.weit.domain.model.CoordinateInfo
import com.weit.domain.model.exception.InvalidPermissionException
import com.weit.domain.model.exception.InvalidRequestException
import com.weit.domain.model.exception.UnKnownException
import dagger.hilt.android.qualifiers.ActivityContext
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PlaceDateSource @Inject constructor(
    @ActivityContext private val context: Context,
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
        Place.Field.LAT_LNG
    )

    suspend fun getPlace(placeId: String): Place = callbackFlow {
        val request = FetchPlaceRequest.builder(placeId, defaultPlaceField).build()
        placesClient.fetchPlace(request).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                trySend(task.result.place)
            } else {
                throw task.exception ?: UnKnownException()
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

                if (metadata == null || metadata.isEmpty()) {
                    return@addOnSuccessListener
                }

                val photoMetadata = metadata.first()
                val photoRequest = FetchPhotoRequest.builder(photoMetadata)
                    .build()

                placesClient.fetchPhoto(photoRequest)
                    .addOnSuccessListener { fetchPhotoResponse: FetchPhotoResponse ->
                        trySend(fetchPhotoResponse.bitmap)
                    }.addOnFailureListener {
                        throw it
                    }
            }.addOnFailureListener {
               throw it
            }
        awaitClose { }
    }

    suspend fun getCurrentPlaceDetail(): Result<CoordinateInfo> {
        val accessFineLocation =
            checkLocationPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
        val accessCoarseLocation =
            checkLocationPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION)

        return if (accessFineLocation.isGranted && accessCoarseLocation.isGranted) {
            var latitude: Float? = null
            var longitude: Float? = null
            val fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(context)
            fusedLocationProviderClient.lastLocation
                .addOnSuccessListener {
                    latitude = it.latitude.toFloat()
                    longitude = it.longitude.toFloat()
                }.await()
            if (latitude == null || longitude == null){
                Result.failure(InvalidRequestException())
            } else {
                Result.success(CoordinateInfo(latitude!!, longitude!!))
            }
        } else {
            Result.failure(InvalidPermissionException())
        }
    }

    private suspend fun checkLocationPermission(permission: String): TedPermissionResult {
        return TedPermission.create()
            .setDeniedMessage("현재 위치 정보를 가져오기 위해서는 권한이 필요해요")
            .setPermissions(permission)
            .check()
    }
}
