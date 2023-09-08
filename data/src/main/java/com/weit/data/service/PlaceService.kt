package com.weit.data.service

import com.weit.data.model.map.GeocodingResult
import com.weit.data.model.place.PlaceAutoCompleteResponse
import retrofit2.http.GET
import retrofit2.http.Query

private const val GEOCODE_URL = "https://maps.googleapis.com/maps/api/geocode/json"
private const val AUTOCOMPLETE_URL = "https://maps.googleapis.com/maps/api/place/autocomplete/json"

interface PlaceService {

    @GET(GEOCODE_URL)
    suspend fun getPlaceDetail(
        @Query("key") apiKey: String,
        @Query("place_id") placeId: String,
    ): GeocodingResult

    @GET(GEOCODE_URL)
    suspend fun getPlacesByCoordinate(
        @Query("key") apiKey: String,
        @Query("latlng") latlng: String,
        @Query("language") lang: String,
        @Query("result_type") resultType: String,
    ): GeocodingResult

    @GET(AUTOCOMPLETE_URL)
    suspend fun getPlacesByQuery(
        @Query("key") apiKey: String,
        @Query("input") query: String,
        @Query("language") lang: String,
        @Query("types") types: String,
        @Query("rankby") rankby: String = "distance",
    ): PlaceAutoCompleteResponse
}
