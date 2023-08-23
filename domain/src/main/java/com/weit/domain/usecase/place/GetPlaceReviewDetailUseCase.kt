package com.weit.domain.usecase.place

import com.weit.domain.model.exception.InvalidTokenException
import com.weit.domain.model.place.PlaceReviewByPlaceIdInfo
import com.weit.domain.repository.place.PlaceReviewRepository
import com.weit.domain.repository.user.UserRepository
import javax.inject.Inject

class GetPlaceReviewDetailUseCase @Inject constructor(
    private val placeReviewRepository: PlaceReviewRepository,
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(placeId: String): String {
        val isExistReview = placeReviewRepository.isExistReview(placeId).getOrNull()
        val getUserId = userRepository.getUser().getOrNull()?.userID
        var result = ""

        if (isExistReview == true && getUserId != null) {
            val info = PlaceReviewByPlaceIdInfo(placeId)
            val getReviewByPlaceId = placeReviewRepository.getByPlaceId(info).getOrNull()

            if (getReviewByPlaceId != null) {
                for (item in getReviewByPlaceId) {
                    if (item.placeId.equals(getReviewByPlaceId)) {
                        result = item.review
                    }
                }
            }
        }
        return result
    }
}
