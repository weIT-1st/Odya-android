package com.weit.data.repository.favoritePlace

import com.weit.data.model.favoritePlace.FavoritePlaceRegistration
import com.weit.data.source.FavoritePlaceDateSource
import com.weit.domain.model.exception.UnKnownException
import com.weit.domain.model.exception.auth.DuplicatedSomethingException
import com.weit.domain.model.exception.auth.InvalidSomethingException
import com.weit.domain.model.exception.auth.NeedUserRegistrationException
import com.weit.domain.model.exception.favoritePlace.ExistedPlaceIdException
import com.weit.domain.model.exception.favoritePlace.InvalidRequestException
import com.weit.domain.model.exception.favoritePlace.InvalidTokenException
import com.weit.domain.model.exception.favoritePlace.NotExistPlaceIdException
import com.weit.domain.model.favoritePlace.FavoritePlaceDetail
import com.weit.domain.model.favoritePlace.FavoritePlaceInfo
import com.weit.domain.model.place.PlaceReviewDetail
import com.weit.domain.repository.favoritePlace.FavoritePlaceRepository
import okhttp3.internal.http.HTTP_BAD_REQUEST
import okhttp3.internal.http.HTTP_CONFLICT
import okhttp3.internal.http.HTTP_NOT_FOUND
import okhttp3.internal.http.HTTP_UNAUTHORIZED
import retrofit2.HttpException
import javax.inject.Inject

class FavoritePlaceRepositoryImpl @Inject constructor(
    private val dataSource: FavoritePlaceDateSource,
) : FavoritePlaceRepository {
    override suspend fun register(placeId: String): Result<Unit> {
        val result = runCatching {
            dataSource.register(FavoritePlaceRegistration(placeId))
        }
        return if (result.isSuccess){
            Result.success(Unit)
        } else {
            Result.failure(handleFavoritePlaceError(result.exceptionOrNull()!!))
        }
    }

    override suspend fun delete(favoritePlaceId: Long): Result<Unit> {
        val result = runCatching {
            dataSource.delete(favoritePlaceId)
        }
        return if (result.isSuccess){
            Result.success(Unit)
        } else {
            Result.failure(handleFavoritePlaceError(result.exceptionOrNull()!!))
        }
    }

    override suspend fun isFavoritePlace(placeId: String): Result<Boolean> {
        val result = runCatching {
            dataSource.isFavoritePlace(placeId)
        }
        return if (result.isSuccess){
            Result.success(result.getOrThrow())
        } else {
            Result.failure(handleFavoritePlaceError(result.exceptionOrNull()!!))
        }
    }

    override suspend fun getFavoritePlaceCount(): Result<Int> {
        val result = runCatching {
            dataSource.getFavoritePlaceCount()
        }
        return if (result.isSuccess){
            Result.success(result.getOrThrow())
        } else {
            Result.failure(handleFavoritePlaceError(result.exceptionOrNull()!!))
        }
    }

    override suspend fun getFavoritePlaces(favoritePlaceInfo: FavoritePlaceInfo): Result<List<FavoritePlaceDetail>> {
        val result = runCatching {
            dataSource.getFavoritePlaces(favoritePlaceInfo).content.map{
                FavoritePlaceDetail(
                    favoritePlaceId = it.favoritePlaceId,
                    placeId = it.placeId,
                    userId = it.userId
                )
            }
        }
        return if (result.isSuccess){
            Result.success(result.getOrThrow())
        } else {
            Result.failure(handleFavoritePlaceError(result.exceptionOrNull()!!))
        }
    }

    private fun handleFavoritePlaceError(t: Throwable): Throwable {
        return if (t is HttpException) {
            when (t.code()) {
                HTTP_CONFLICT -> ExistedPlaceIdException()
                HTTP_BAD_REQUEST -> InvalidRequestException()
                HTTP_UNAUTHORIZED -> InvalidTokenException()
                HTTP_NOT_FOUND -> NotExistPlaceIdException()
                else -> UnKnownException()
            }
        } else {
            t
        }
    }

}
