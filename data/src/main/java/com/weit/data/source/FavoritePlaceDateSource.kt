package com.weit.data.source

import com.weit.data.model.favoritePlace.FavoritePlaceListDTO
import com.weit.data.model.favoritePlace.FavoritePlaceRegistration
import com.weit.data.model.place.PlaceReviewListDTO
import com.weit.data.model.place.PlaceReviewModification
import com.weit.data.model.place.PlaceReviewRegistration
import com.weit.data.service.FavoritePlaceService
import com.weit.domain.model.favoritePlace.FavoritePlaceInfo
import com.weit.domain.model.place.PlaceReviewByPlaceIdInfo
import com.weit.domain.model.place.PlaceReviewByUserIdInfo
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

    suspend fun getFavoritePlaces(info: FavoritePlaceInfo): FavoritePlaceListDTO =
        service.getFavoirtePlaces(
           size = info.size,
           sortType = info.sortType,
           lastFavoritePlaceId = info.lastFavoritePlaceId
        )
}
