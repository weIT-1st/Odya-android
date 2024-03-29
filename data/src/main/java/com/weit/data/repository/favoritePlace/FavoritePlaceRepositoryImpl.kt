package com.weit.data.repository.favoritePlace

import com.weit.data.model.favoritePlace.FavoritePlaceRegistration
import com.weit.data.source.FavoritePlaceDateSource
import com.weit.data.util.exception
import com.weit.domain.model.exception.InvalidRequestException
import com.weit.domain.model.exception.InvalidTokenException
import com.weit.domain.model.exception.NoMoreItemException
import com.weit.domain.model.exception.UnKnownException
import com.weit.domain.model.exception.favoritePlace.NotExistPlaceIdException
import com.weit.domain.model.exception.favoritePlace.RegisteredFavoritePlaceException
import com.weit.domain.model.favoritePlace.FavoritePlaceDetail
import com.weit.domain.model.favoritePlace.FavoritePlaceInfo
import com.weit.domain.model.favoritePlace.FriendFavoritePlaceInfo
import com.weit.domain.repository.favoritePlace.FavoritePlaceRepository
import okhttp3.internal.http.HTTP_BAD_REQUEST
import okhttp3.internal.http.HTTP_CONFLICT
import okhttp3.internal.http.HTTP_NOT_FOUND
import okhttp3.internal.http.HTTP_UNAUTHORIZED
import retrofit2.HttpException
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

class FavoritePlaceRepositoryImpl @Inject constructor(
    private val dataSource: FavoritePlaceDateSource,
) : FavoritePlaceRepository {

    private val hasNextFavoritePlace = AtomicBoolean(true)
    private val hasNextFriendFavoritePlace = AtomicBoolean(true)

    override suspend fun register(placeId: String): Result<Unit> {
        return handleFavoritePlaceResult {
            dataSource.register(FavoritePlaceRegistration(placeId))
        }
    }

    override suspend fun delete(favoritePlaceId: Long): Result<Unit> {
        return handleFavoritePlaceResult {
            dataSource.delete(favoritePlaceId)
        }
    }

    override suspend fun deleteByPlaceId(placeId: String): Result<Unit> {
        return handleFavoritePlaceResult {
            dataSource.deleteByPlaceId(placeId)
        }
    }

    override suspend fun isFavoritePlace(placeId: String): Result<Boolean> {
        return handleFavoritePlaceResult {
            dataSource.isFavoritePlace(placeId)
        }
    }

    override suspend fun getFavoritePlaceCount(): Result<Int> {
        return handleFavoritePlaceResult {
            dataSource.getFavoritePlaceCount()
        }
    }

    override suspend fun getFriendPlaceCount(userId: Long): Result<Int> {
        return handleFavoritePlaceResult {
            dataSource.getFriendFavoritePlaceCount(userId)
        }
    }

    override suspend fun getFavoritePlaces(favoritePlaceInfo: FavoritePlaceInfo): Result<List<FavoritePlaceDetail>> {
        if(favoritePlaceInfo.lastFavoritePlaceId == null){
            hasNextFavoritePlace.set(true)
        }

        if (hasNextFavoritePlace.get().not()) {
            return Result.failure(NoMoreItemException())
        }
        val result = runCatching {
            dataSource.getFavoritePlaces(favoritePlaceInfo)
        }
        return if (result.isSuccess) {
            val places = result.getOrThrow()
            hasNextFavoritePlace.set(places.hasNext)
            Result.success(places.content.map {
                FavoritePlaceDetail(
                    favoritePlaceId = it.favoritePlaceId,
                    placeId = it.placeId,
                    userId = it.userId,
                    isFavoritePlace = it.isFavoritePlace
                )})
        } else {
            Result.failure(handleFavoritePlaceError(result.exception()))
        }
    }

    override suspend fun getFriendFavoritePlaces(friendFavoritePlaceInfo: FriendFavoritePlaceInfo): Result<List<FavoritePlaceDetail>> {
        if(friendFavoritePlaceInfo.lastFavoritePlaceId == null){
            hasNextFriendFavoritePlace.set(true)
        }

        if (hasNextFriendFavoritePlace.get().not()) {
            return Result.failure(NoMoreItemException())
        }
        val result = runCatching {
            dataSource.getFriendFavoritePlaces(friendFavoritePlaceInfo)
        }
        return if (result.isSuccess) {
            val places = result.getOrThrow()
            hasNextFriendFavoritePlace.set(places.hasNext)
            Result.success(places.content.map {
                FavoritePlaceDetail(
                    favoritePlaceId = it.favoritePlaceId,
                    placeId = it.placeId,
                    userId = it.userId,
                    isFavoritePlace = it.isFavoritePlace
                )})
        } else {
            Result.failure(handleFavoritePlaceError(result.exception()))
        }    }

    private fun handleFavoritePlaceError(t: Throwable): Throwable {
        return if (t is HttpException) {
            when (t.code()) {
                HTTP_CONFLICT -> RegisteredFavoritePlaceException()
                HTTP_BAD_REQUEST -> InvalidRequestException()
                HTTP_UNAUTHORIZED -> InvalidTokenException()
                HTTP_NOT_FOUND -> NotExistPlaceIdException()
                else -> UnKnownException()
            }
        } else {
            t
        }
    }

    private inline fun <T> handleFavoritePlaceResult(
        block: () -> T,
    ): Result<T> {
        return try {
            val result = runCatching(block)
            if (result.isSuccess) {
                Result.success(result.getOrThrow())
            } else {
                Result.failure(handleFavoritePlaceError(result.exceptionOrNull()!!))
            }
        } catch (t: Throwable) {
            Result.failure(handleFavoritePlaceError(t))
        }
    }
}
