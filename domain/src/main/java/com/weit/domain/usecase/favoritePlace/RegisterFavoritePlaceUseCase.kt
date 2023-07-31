package com.weit.domain.usecase.favoritePlace

import com.weit.domain.model.favoritePlace.FavoritePlaceRegistrationInfo
import com.weit.domain.model.place.PlaceReviewRegistrationInfo
import com.weit.domain.repository.favoritePlace.FavoritePlaceRepository
import javax.inject.Inject

class RegisterFavoritePlaceUseCase @Inject constructor(
    private val repository: FavoritePlaceRepository,
) {
    suspend operator fun invoke(placeId: String): Result<Unit> =
        repository.register(placeId)
}
