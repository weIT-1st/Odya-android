package com.weit.domain.repository.place

import com.weit.domain.model.CoordinateInfo
import com.weit.domain.model.place.PlaceDetail
import com.weit.domain.model.place.PlacePrediction

interface PlaceRepository {
    suspend fun searchPlace(query: String): List<PlacePrediction>

    suspend fun getPlaceDetail(placeId: String): Result<PlaceDetail>

    suspend fun getPlacesByCoordinate(latitude: Double, longitude: Double): Result<List<PlacePrediction>>

    suspend fun getPlaceImage(placeId: String): Result<ByteArray>

    suspend fun getCurrentPlaceDetail(): Result<CoordinateInfo>
}
