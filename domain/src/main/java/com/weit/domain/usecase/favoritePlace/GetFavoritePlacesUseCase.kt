package com.weit.domain.usecase.favoritePlace

import com.weit.domain.model.favoritePlace.FavoritePlaceDetail
import com.weit.domain.model.favoritePlace.FavoritePlaceInfo
import com.weit.domain.repository.favoritePlace.FavoritePlaceRepository
import javax.inject.Inject

class GetFavoritePlacesUseCase @Inject constructor(
    private val repository: FavoritePlaceRepository,
) {
    suspend operator fun invoke(favoritePlaceInfo: FavoritePlaceInfo): Result<List<FavoritePlaceDetail>> =
        repository.getFavoritePlaces(favoritePlaceInfo)
}
