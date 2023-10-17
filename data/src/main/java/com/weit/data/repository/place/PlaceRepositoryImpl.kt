package com.weit.data.repository.place

import com.weit.data.model.map.Place
import com.weit.data.source.ImageDataSource
import com.weit.data.source.PlaceDateSource
import com.weit.data.util.exception
import com.weit.domain.model.CoordinateInfo
import com.weit.domain.model.exception.ImageNotFoundException
import com.weit.domain.model.exception.InvalidRequestException
import com.weit.domain.model.place.PlaceDetail
import com.weit.domain.model.place.PlacePrediction
import com.weit.domain.repository.place.PlaceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class PlaceRepositoryImpl @Inject constructor(
    private val dataSource: PlaceDateSource,
    private val imageDataSource: ImageDataSource
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

    override suspend fun getPlaceDetail(placeId: String): Result<PlaceDetail> {
        val result = runCatching {dataSource.getPlace(placeId)}

        return if (result.isSuccess) {
            val placeDetail = result.getOrThrow()
            Result.success(
                PlaceDetail(
                    placeDetail.id,
                    placeDetail.name,
                    placeDetail.address,
                    placeDetail.latLng?.latitude,
                    placeDetail.latLng?.longitude
                )
            )
        } else {
            Result.failure(result.exception())
        }
    }

    override suspend fun getPlacesByCoordinate(
        latitude: Double,
        longitude: Double
    ): Result<List<PlacePrediction>> {
        val geocodingResult = dataSource.getPlacesByCoordinate(latitude, longitude)
        val result = geocodingResult.result.map { place ->
            CoroutineScope(Dispatchers.IO).async {
                getPlacePrediction(place)
            }
        }.awaitAll().filterNotNull()

        return if (result.isEmpty().not()){
            Result.success(result)
        } else {
            Result.failure(InvalidRequestException())
        }
    }

    override suspend fun getPlaceImage(placeId: String): Result<ByteArray> {
        val result = runCatching { dataSource.getPlaceImage(placeId) }
        return if (result.isSuccess) {
            val placeImage = result.getOrThrow().firstOrNull()

            if (placeImage != null){
                Result.success(imageDataSource.getCompressedBytes(placeImage))
            } else {
                Result.failure(ImageNotFoundException())
            }
        } else {
            Result.failure(result.exception())
        }
    }

    override suspend fun getCurrentPlaceDetail(): Result<CoordinateInfo> =
        dataSource.getCurrentPlaceDetail()

    private suspend fun getPlacePrediction(place: Place): PlacePrediction? {
        // TODO 개선 필요. api를 두번 호출해야 업체명을 안다는게 말이안됨
        // 역지오코딩 할 때 우리가 직접만든 place 객체가 아니라 구글 지도에서 제공하는 place 객체를 역직렬화 한다면
        // 더 정상적인 데이터가 나오지 않을까
        val placeId = place.placeId ?: return null
        val result = dataSource.getPlace(placeId)
        return PlacePrediction(
            placeId = placeId,
            name = result.name ?: "",
            address = result.address ?: "",
        )
    }
}
