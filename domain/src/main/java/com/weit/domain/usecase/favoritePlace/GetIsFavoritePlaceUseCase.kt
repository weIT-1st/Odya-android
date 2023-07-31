package com.weit.domain.usecase.favoritePlace

import com.weit.domain.model.place.PlaceDetail
import com.weit.domain.repository.favoritePlace.FavoritePlaceRepository
import com.weit.domain.repository.place.PlaceRepository
import javax.inject.Inject

class GetIsFavoritePlaceUseCase @Inject constructor(
    private val repository: FavoritePlaceRepository,
) {
    suspend operator fun invoke(placeId: String): Result<Boolean> =
        repository.isFavoritePlace(placeId)
}
