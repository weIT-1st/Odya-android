package com.weit.domain.usecase.place

import com.weit.domain.model.exception.UnKnownException
import com.weit.domain.model.place.PlaceReviewByPlaceIdQuery
import com.weit.domain.model.place.PlaceReviewInfo
import com.weit.domain.repository.place.PlaceReviewRepository
import com.weit.domain.repository.user.UserRepository
import com.weit.domain.usecase.user.GetUserByNicknameUseCase
import javax.inject.Inject

class GetPlaceReviewContentUseCase @Inject constructor(
    private val getUserByNicknameUseCase: GetUserByNicknameUseCase,
    private val placeReviewRepository: PlaceReviewRepository,
    private val userRepository: UserRepository,
) {

    suspend operator fun invoke(placeId: String): Result<List<PlaceReviewInfo>> {
        val userId = userRepository.getUserId()
        val placeReviewResult = placeReviewRepository.getByPlaceId(PlaceReviewByPlaceIdQuery(placeId))

        return if (placeReviewResult.isSuccess) {
            val review = placeReviewResult.getOrThrow()
            val list = review.map {
                PlaceReviewInfo(
                    it.writerNickname,
                    (it.starRating.toFloat() / 2),
                    it.review,
                    it.createdAt.toString(),
                    it.userId,
                    it.userId == userId,
                    it.id,
                    getUserByNicknameUseCase(it.writerNickname).getOrNull()?.profile,
                )
            }
            val myReview = list.find { it.userId == userId }
            return if (myReview == null) {
                Result.success(list)
            } else {
                val listWithMyReview = list.toMutableList()
                listWithMyReview.remove(myReview)
                listWithMyReview.add(0, myReview)

                Result.success(listWithMyReview)
            }
        } else {
            val exception = placeReviewResult.exceptionOrNull()
            if (exception == null) {
                Result.failure(UnKnownException())
            } else {
                Result.failure(exception)
            }
        }
    }
}
