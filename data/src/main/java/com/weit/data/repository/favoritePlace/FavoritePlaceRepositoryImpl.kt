package com.weit.data.repository.favoritePlace

import com.weit.data.model.favoritePlace.FavoritePlaceRegistration
import com.weit.data.source.FavoritePlaceDateSource
import com.weit.domain.model.favoritePlace.FavoritePlaceDetail
import com.weit.domain.model.favoritePlace.FavoritePlaceInfo
import com.weit.domain.model.place.PlaceReviewDetail
import com.weit.domain.repository.favoritePlace.FavoritePlaceRepository
import javax.inject.Inject

class FavoritePlaceRepositoryImpl @Inject constructor(
    private val dataSource: FavoritePlaceDateSource,
) : FavoritePlaceRepository {
    override suspend fun register(placeId: String): Result<Unit> {
        return runCatching {
            dataSource.register(FavoritePlaceRegistration(placeId))
        }
    }

    override suspend fun delete(favoritePlaceId: Long): Result<Unit> {
        return runCatching {
            dataSource.delete(favoritePlaceId)
        }
    }

    override suspend fun isFavoritePlace(placeId: String): Result<Boolean> {
        return runCatching {
            dataSource.isFavoritePlace(placeId)
        }
    }

    override suspend fun getFavoritePlaceCount(): Result<Int> {
        return runCatching {
            dataSource.getFavoritePlaceCount()
        }
    }

    override suspend fun getFavoritePlaces(favoritePlaceInfo: FavoritePlaceInfo): Result<List<FavoritePlaceDetail>> {
        return runCatching {
            dataSource.getFavoritePlaces(favoritePlaceInfo).content.map{
                FavoritePlaceDetail(
                    favoritePlaceId = it.favoritePlaceId,
                    placeId = it.placeId,
                    userId = it.userId
                )
            }
        }
    }

}
