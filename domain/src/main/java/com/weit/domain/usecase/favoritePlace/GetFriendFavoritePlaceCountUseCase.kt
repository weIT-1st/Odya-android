package com.weit.domain.usecase.favoritePlace

import com.weit.domain.repository.favoritePlace.FavoritePlaceRepository
import javax.inject.Inject

class GetFriendFavoritePlaceCountUseCase @Inject constructor(
    private val repository: FavoritePlaceRepository,
) {
    suspend operator fun invoke(userId: Long): Result<Int> =
        repository.getFriendPlaceCount(userId)
}
