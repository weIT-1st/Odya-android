package com.weit.data.repository.place

import com.weit.data.source.PlaceReviewDateSource
import com.weit.domain.model.place.PlaceReviewRegistrationInfo
import com.weit.domain.model.place.PlaceReviewUpdateInfo
import com.weit.domain.repository.place.PlaceReviewRepository
import javax.inject.Inject

class PlaceReviewRepositoryImpl @Inject constructor(
    private val dataSource: PlaceReviewDateSource,
) : PlaceReviewRepository {

    override suspend fun register(info: PlaceReviewRegistrationInfo): Result<Unit> {
        return runCatching {
            dataSource.register(info)
        }
    }


    override suspend fun update(info: PlaceReviewUpdateInfo): Result<Unit> {
        return runCatching {
            dataSource.update(info)
        }
    }

    override suspend fun delete(id: Long): Result<Unit> {
        return runCatching {
            dataSource.delete(id)
        }
    }


}
