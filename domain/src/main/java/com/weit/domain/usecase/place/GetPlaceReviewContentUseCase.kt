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

        return if (placeReviewResult.isSuccess){
            val review = placeReviewResult.getOrThrow()
            val list = review.map {
                val profile = getUserByNicknameUseCase(it.writerNickname).getOrNull()
                if (profile != null) {
                    PlaceReviewInfo(
                        it.writerNickname,
                        (it.starRating / 2).toFloat(),
                        it.review,
                        it.createdAt.toString(),
                        it.userId,
                        it.userId == userId,
                        it.id,
                        profile.profile)
                } else {
                    PlaceReviewInfo(
                        it.writerNickname,
                        (it.starRating / 2).toFloat(),
                        it.review,
                        it.createdAt.toString(),
                        it.userId,
                        it.userId == userId,
                        it.id,
                        profile)
                }
            }
            val myReview = list.find{it.userId == userId}
            return if (myReview == null) {
                Result.success(list)
            } else {
                val mutableList = list.toMutableList()
                mutableList.remove(myReview)
                mutableList.add(0, myReview)

                Result.success(mutableList.toList())
            }
        } else {
            val exception = placeReviewResult.exceptionOrNull()
            if (exception == null){
                Result.failure(UnKnownException())
            } else {
                Result.failure(exception)
            }
        }
    }
}
