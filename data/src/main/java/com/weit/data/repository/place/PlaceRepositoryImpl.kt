package com.weit.data.repository.place

import android.graphics.Bitmap
import android.util.Log
import com.google.android.gms.common.api.ApiException
import com.weit.data.model.map.Place
import com.weit.data.source.PlaceDateSource
import com.weit.domain.model.place.PlaceDetail
import com.weit.domain.model.place.PlacePrediction
import com.weit.domain.repository.place.PlaceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.first
import java.io.ByteArrayOutputStream
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

    override suspend fun getPlaceDetail(placeId: String): Result<PlaceDetail> {
        val result = dataSource.getPlace(placeId)

        return if (result is Throwable) {
            Result.failure(result)
        } else {
            Result.success(
                PlaceDetail(
                    result.id,
                    result.name,
                    result.address,
                    result.latLng?.latitude,
                    result.latLng?.longitude
                )
            )
        }
    }

    override suspend fun getPlacesByCoordinate(
        latitude: Double,
        longitude: Double
    ): List<PlacePrediction> {
        val geocodingResult = dataSource.getPlacesByCoordinate(latitude, longitude)
        return geocodingResult.result.map { place ->
            CoroutineScope(Dispatchers.IO).async {
                getPlacePrediction(place)
            }
        }.awaitAll().filterNotNull()
    }

    override suspend fun getPlaceImage(placeId: String): ByteArray? {
        val result = dataSource.getPlaceImage(placeId).first()
        return if (result == null) {
            null
        } else {
            bitmapToArray(result)
        }
    }

    private suspend fun getPlacePrediction(place: Place): PlacePrediction? {
        // TODO 개선 필요. api를 두번 호출해야 업체명을 안다는게 말이안됨
        // 역지오코딩 할 때 우리가 직접만든 place 객체가 아니라 구글 지도에서 제공하는 place 객체를 역직렬화 한다면
        // 더 정상적인 데이터가 나오지 않을까
        val placeId = place.placeId ?: return null
        val result = dataSource.getPlace(placeId) ?: return null
        return PlacePrediction(
            placeId = placeId,
            name = result.name ?: "",
            address = result.address ?: "",
        )
    }

    private fun bitmapToArray(bitmap: Bitmap): ByteArray {
        var outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, outputStream)
        return outputStream.toByteArray()
    }
}
