package com.weit.data.repository.place

import com.weit.data.model.map.Place
import com.weit.data.source.PlaceDateSource
import com.weit.domain.model.place.PlaceDetail
import com.weit.domain.model.place.PlacePrediction
import com.weit.domain.repository.place.PlaceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import javax.inject.Inject

class PlaceRepositoryImpl @Inject constructor(
    private val dataSource: PlaceDateSource,
) : PlaceRepository {

    override suspend fun searchPlace(query: String): List<PlacePrediction> {
        val result = dataSource.searchPlaces(query)
        return result.map {
            PlacePrediction(
                it.placeId,
                it.getPrimaryText(null).toString(),
                it.getSecondaryText(null).toString(),
            )
        }
    }

    override suspend fun getPlaceDetail(placeId: String): PlaceDetail {
        val result = dataSource.getPlace(placeId)
        return PlaceDetail(
            result?.id ?: "",
            result?.name,
            result?.address,
            result?.latLng?.latitude,
            result?.latLng?.longitude,
        )
    }

    override suspend fun getPlacesByCoordinate(latitude: Double, longitude: Double): List<PlacePrediction> {
        val geocodingResult = dataSource.getPlacesByCoordinate(latitude, longitude)
        return geocodingResult.result.map { place ->
            CoroutineScope(Dispatchers.IO).async {
                getPlacePrediction(place)
            }
        }.awaitAll().filterNotNull()
    }

    private suspend fun getPlacePrediction(place: Place): PlacePrediction? {
        // TODO 개선 필요. api를 두번 호출해야 업체명을 안다는게 말이안됨
        // 역지오코딩 할 때 우리가 직접만든 place 객체가 아니라 구글 지도에서 제공하는 place 객체를 역직렬화 한다면
        // 더 정상적인 데이터가 나오지 않을까
        val placeId = place.placeId
        val result = dataSource.getPlace(placeId) ?: return null
        return PlacePrediction(
            placeId = placeId,
            name = result.name ?: "",
            address = result.address ?: "",
        )
    }
}
