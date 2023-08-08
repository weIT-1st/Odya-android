package com.weit.domain.usecase.favoritePlace

import com.weit.domain.repository.favoritePlace.FavoritePlaceRepository
import javax.inject.Inject

class DeleteFavoritePlaceUseCase @Inject constructor(
    private val repository: FavoritePlaceRepository,
) {
    suspend operator fun invoke(favoritePlaceId: Long): Result<Unit> =
        repository.delete(favoritePlaceId)
}
