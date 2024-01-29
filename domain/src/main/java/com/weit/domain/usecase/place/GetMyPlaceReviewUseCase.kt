package com.weit.domain.usecase.place

import com.weit.domain.model.exception.UnKnownException
import com.weit.domain.model.place.PlaceMyReviewInfo
import com.weit.domain.model.place.PlaceReviewByUserIdQuery
import com.weit.domain.repository.place.PlaceRepository
import com.weit.domain.repository.place.PlaceReviewRepository
import com.weit.domain.repository.user.UserRepository
import javax.inject.Inject
class GetMyPlaceReviewUseCase @Inject constructor(
    private val placeReviewRepository: PlaceReviewRepository,
    private val userRepository: UserRepository,
    private val placeRepository: PlaceRepository
) {
    suspend operator fun invoke(size: Int = 10, lastId: Long? = null): Result<List<PlaceMyReviewInfo>> {
        val myId = userRepository.getUserId()

        val reviewResult = placeReviewRepository.getByUserId(
            PlaceReviewByUserIdQuery(
                userId = myId,
                size = size,
                lastPlaceReviewId = lastId)
        )

        return if (reviewResult.isSuccess){
            val reviewList = reviewResult.getOrNull() ?: emptyList()
            val list = reviewList.map {
                PlaceMyReviewInfo(
                    (placeRepository.getPlaceDetail(it.placeId).getOrThrow().name ?: ""),
                    (it.starRating.toFloat() / 2),
                    it.review,
                    it.createdAt,
                    it.placeReviewId
                )
            }
            Result.success(list)
        } else {
            Result.failure(reviewResult.exceptionOrNull() ?: UnKnownException())
        }
    }
}

