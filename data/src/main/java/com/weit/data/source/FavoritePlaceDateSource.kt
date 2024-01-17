package com.weit.data.source

import com.weit.data.model.ListResponse
import com.weit.data.model.favoritePlace.FavoritePlaceDTO
import com.weit.data.model.favoritePlace.FavoritePlaceRegistration
import com.weit.data.service.FavoritePlaceService
import com.weit.domain.model.exception.InvalidPermissionException
import com.weit.domain.model.exception.InvalidRequestException
import com.weit.domain.model.exception.InvalidTokenException
import com.weit.domain.model.exception.UnKnownException
import com.weit.domain.model.exception.community.ExistedCommunityIdException
import com.weit.domain.model.exception.community.NotExistCommunityIdOrCommunityCommentsException
import com.weit.domain.model.favoritePlace.FavoritePlaceInfo
import com.weit.domain.model.favoritePlace.FriendFavoritePlaceInfo
import okhttp3.internal.http.HTTP_BAD_REQUEST
import okhttp3.internal.http.HTTP_CONFLICT
import okhttp3.internal.http.HTTP_FORBIDDEN
import okhttp3.internal.http.HTTP_NOT_FOUND
import okhttp3.internal.http.HTTP_UNAUTHORIZED
import retrofit2.Response
import javax.inject.Inject

class FavoritePlaceDateSource @Inject constructor(
    private val service: FavoritePlaceService,
) {
    suspend fun register(favoritePlaceRegistration: FavoritePlaceRegistration) {
        service.register(favoritePlaceRegistration)
    }

    suspend fun delete(favoritePlaceId: Long): Response<Unit> {
        return service.delete(favoritePlaceId)
    }

    suspend fun isFavoritePlace(placeId: String): Boolean =
        service.isFavoritePlace(placeId)

    suspend fun getFavoritePlaceCount(): Int =
        service.getFavoritePlaceCount()

    suspend fun getFriendFavoritePlaceCount(userId: Long): Int =
        service.getFriendFavoritePlaceCount(userId)

    suspend fun getFavoritePlaces(info: FavoritePlaceInfo): ListResponse<FavoritePlaceDTO> =
        service.getFavoritePlaces(
            size = info.size,
            sortType = info.sortType,
            lastFavoritePlaceId = info.lastFavoritePlaceId,
        )

    suspend fun getFriendFavoritePlaces(info: FriendFavoritePlaceInfo): ListResponse<FavoritePlaceDTO> =
        service.getFriendFavoritePlaces(
            userId = info.userId,
            size = info.size,
            sortType = info.sortType,
            lastFavoritePlaceId = info.lastFavoritePlaceId,
        )
}
