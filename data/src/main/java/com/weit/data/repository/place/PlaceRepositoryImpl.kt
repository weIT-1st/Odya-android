package com.weit.data.repository.place

import com.weit.data.source.PlaceDateSource
import com.weit.domain.model.place.PlaceDetail
import com.weit.domain.model.place.PlacePrediction
import com.weit.domain.repository.place.PlaceRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class PlaceRepositoryImpl @Inject constructor(
    private val dataSource: PlaceDateSource,
) : PlaceRepository {

    override suspend fun searchPlace(query: String): List<PlacePrediction> {
        val result = dataSource.searchPlaces(query).first()
        return result.map {
            PlacePrediction(
                it.placeId,
                it.getPrimaryText(null).toString(),
                it.getSecondaryText(null).toString(),
            )
        }
    }

    override suspend fun getPlaceDetail(placeId: String): PlaceDetail {
        val result = dataSource.getPlaceDetail(placeId).result[0]
        return PlaceDetail(
            result.placeId,
            result.name,
            result.adrAddress,
            result.geometry?.location?.lat,
            result.geometry?.location?.lng,
        )
    }
}
