package com.weit.domain.repository.favoritePlace

import com.weit.domain.model.favoritePlace.FavoritePlaceDetail
import com.weit.domain.model.favoritePlace.FavoritePlaceInfo
import com.weit.domain.model.favoritePlace.FriendFavoritePlaceInfo

interface FavoritePlaceRepository {
    suspend fun register(
        placeId: String,
    ): Result<Unit>

    suspend fun delete(
        favoritePlaceId: Long,
    ): Result<Unit>

    suspend fun deleteByPlaceId(
        placeId: String,
    ): Result<Unit>

    suspend fun isFavoritePlace(
        placeId: String,
    ): Result<Boolean>

    suspend fun getFavoritePlaceCount(): Result<Int>
    suspend fun getFriendPlaceCount(
        userId: Long
    ): Result<Int>

    suspend fun getFavoritePlaces(
        favoritePlaceInfo: FavoritePlaceInfo,
    ): Result<List<FavoritePlaceDetail>>

    suspend fun getFriendFavoritePlaces(
        friendFavoritePlaceInfo: FriendFavoritePlaceInfo,
    ): Result<List<FavoritePlaceDetail>>
}
