package com.weit.data.source

import com.weit.data.model.ListResponse
import com.weit.data.model.favoritePlace.FavoritePlaceDTO
import com.weit.data.model.favoritePlace.FavoritePlaceRegistration
import com.weit.data.service.FavoritePlaceService
import com.weit.domain.model.favoritePlace.FavoritePlaceInfo
import javax.inject.Inject

class FavoritePlaceDateSource @Inject constructor(
    private val service: FavoritePlaceService,
) {
    suspend fun register(favoritePlaceRegistration: FavoritePlaceRegistration) {
        service.register(favoritePlaceRegistration)
    }

    suspend fun delete(favoritePlaceId: Long) {
        service.delete(favoritePlaceId)
    }

    suspend fun isFavoritePlace(placeId: String): Boolean =
        service.isFavoritePlace(placeId)

    suspend fun getFavoritePlaceCount(): Int =
        service.getFavoritePlaceCount()

    suspend fun getFavoritePlaces(info: FavoritePlaceInfo): ListResponse<FavoritePlaceDTO> =
        service.getFavoritePlaces(
           size = info.size,
           sortType = info.sortType,
           lastFavoritePlaceId = info.lastFavoritePlaceId
        )
}
