package com.weit.domain.repository.place

import com.weit.domain.model.place.PlaceDetail
import com.weit.domain.model.place.PlacePrediction

interface PlaceRepository {
    suspend fun searchPlace(query: String): List<PlacePrediction>

    suspend fun getPlaceDetail(placeId: String): PlaceDetail

    suspend fun getPlacesByCoordinate(latitude: Double, longitude: Double)
}
