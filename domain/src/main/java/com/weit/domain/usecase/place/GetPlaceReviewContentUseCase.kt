package com.weit.domain.usecase.place

import com.weit.domain.model.exception.NotExistPlaceReviewException
import com.weit.domain.model.exception.auth.ServerLoginFailedException
import com.weit.domain.model.place.PlaceReviewByPlaceIdInfo
import com.weit.domain.model.place.PlaceReviewContentInfo
import com.weit.domain.repository.place.PlaceReviewRepository
import com.weit.domain.repository.user.UserRepository
import javax.inject.Inject

class GetPlaceReviewContentUseCase @Inject constructor(
    private val placeReviewRepository: PlaceReviewRepository,
    private val userRepository: UserRepository,
) {

    suspend operator fun invoke(placeId: String): Result<PlaceReviewContentInfo> {
        val isExistReview = placeReviewRepository.isExistReview(placeId).getOrNull()
        val getUserId = userRepository.getUser().getOrNull()?.userId
            ?: return Result.failure(ServerLoginFailedException())

        if (isExistReview == false) {
            return Result.failure(NotExistPlaceReviewException())
        }

        val info = PlaceReviewByPlaceIdInfo(placeId)
        val getReviewByPlaceId = placeReviewRepository.getByPlaceId(info)

        val myReview = getReviewByPlaceId.getOrNull()?.find { it.userId == getUserId }

        return if (myReview == null) {
            Result.failure(getReviewByPlaceId.exceptionOrNull()!!)
        } else {
            Result.success(PlaceReviewContentInfo(myReview.id, myReview.review, myReview.starRating))
        }
    }
}
