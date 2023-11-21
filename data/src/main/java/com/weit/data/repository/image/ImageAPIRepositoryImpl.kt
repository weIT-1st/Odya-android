package com.weit.data.repository.image

import androidx.datastore.dataStore
import com.weit.data.model.image.PlaceNameDTO
import com.weit.data.source.ImageAPIDataSource
import com.weit.data.util.exception
import com.weit.domain.model.exception.InvalidRequestException
import com.weit.domain.model.exception.InvalidTokenException
import com.weit.domain.model.exception.NoContentException
import com.weit.domain.model.exception.NoMoreItemException
import com.weit.domain.model.exception.UnKnownException
import com.weit.domain.model.image.CoordinateUserImageResponseInfo
import com.weit.domain.model.image.UserImageResponseInfo
import com.weit.domain.model.user.ImageUserType
import com.weit.domain.repository.image.ImageAPIRepository
import okhttp3.internal.http.HTTP_BAD_REQUEST
import okhttp3.internal.http.HTTP_NO_CONTENT
import okhttp3.internal.http.HTTP_UNAUTHORIZED
import retrofit2.HttpException
import retrofit2.Response
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

class ImageAPIRepositoryImpl @Inject constructor(
    private val dataSource: ImageAPIDataSource
): ImageAPIRepository {

    private val hasNextUserImage = AtomicBoolean(true)
    override suspend fun getUserImage(
        size: Int?,
        lastId: Long?
    ): Result<List<UserImageResponseInfo>> {
        if (hasNextUserImage.get().not()){
            return Result.failure(NoMoreItemException())
        }

        val result = kotlin.runCatching { dataSource.getUserImage(size, lastId) }
        return if (result.isSuccess){
            val list = result.getOrThrow()
            Result.success(list.content.map {
                UserImageResponseInfo(
                    it.imageId,
                    it.imageUrl,
                    it.placeId,
                    it.isLifeShot,
                    it.placeName,
                    it.journalId,
                    it.communityId
                )
            })
        } else {
            Result.failure(handleImageError(result.exception()))
        }
    }

    override suspend fun setLifeShot(imageId: Long, placeName: String): Result<Unit> {
        val response = dataSource.setLifeShot(imageId, PlaceNameDTO(placeName))

        return if (response.isSuccessful){
            Result.success(Unit)
        } else {
            Result.failure(handleImageError(handleResponseError(response)))
        }
    }

    override suspend fun deleteLifeShot(imageId: Long): Result<Unit> {
        val response = dataSource.deleteLifeShot(imageId)

        return if (response.isSuccessful){
            Result.success(Unit)
        } else {
            Result.failure(handleImageError(handleResponseError(response)))
        }
    }

    override suspend fun getCoordinateUserImage(
        leftLongitude: Double,
        bottomLatitude: Double,
        rightLongitude: Double,
        topLatitude: Double,
        size: Int?
    ): Result<List<CoordinateUserImageResponseInfo>> {
        val result = runCatching {
            dataSource.getCoordinateUserImage(
                leftLongitude, bottomLatitude, rightLongitude, topLatitude, size
            )
        }

        return if (result.isSuccess){
            val list = result.getOrThrow()
            Result.success(list.map {
                CoordinateUserImageResponseInfo(
                    it.imageId,
                    it.userId,
                    it.imageUrl,
                    it.placeId,
                    it.latitude,
                    it.longitude,
                    setImageUserType(it.imageUserType),
                    it.journalId,
                    it.communityId
                )
            })
        } else {
            Result.failure(handleImageError(result.exception()))
        }
    }

    private fun handleResponseError(response: Response<*>): Throwable {
        return handleCode(response.code())
    }

    private fun handleImageError(t: Throwable): Throwable {
        return if (t is HttpException) {
            handleCode(t.code())
        } else {
            t
        }
    }

    private fun handleCode(code: Int): Throwable {
        return when (code) {
            HTTP_BAD_REQUEST -> InvalidRequestException() // 400
            HTTP_UNAUTHORIZED -> InvalidTokenException()// 401
            HTTP_NO_CONTENT -> NoContentException() // 204
            else -> UnKnownException()
        }
    }

    private fun setImageUserType(type: String): ImageUserType =
        when (type){
            "USER" -> ImageUserType.USER
            "FRIEND" -> ImageUserType.FRIEND
            else -> ImageUserType.OTHER
        }
}
