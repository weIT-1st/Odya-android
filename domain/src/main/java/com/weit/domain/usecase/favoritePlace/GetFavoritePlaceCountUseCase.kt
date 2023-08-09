package com.weit.domain.usecase.favoritePlace

import com.weit.domain.repository.favoritePlace.FavoritePlaceRepository
import javax.inject.Inject

class GetFavoritePlaceCountUseCase @Inject constructor(
    private val repository: FavoritePlaceRepository,
) {
    suspend operator fun invoke(): Result<Int> =
        repository.getFavoritePlaceCount()
}
