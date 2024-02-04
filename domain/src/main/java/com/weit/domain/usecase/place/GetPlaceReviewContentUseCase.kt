package com.weit.domain.usecase.place

import com.weit.domain.model.exception.UnKnownException
import com.weit.domain.model.place.PlaceReviewByPlaceIdQuery
import com.weit.domain.model.place.PlaceReviewContent
import com.weit.domain.model.place.PlaceReviewInfo
import com.weit.domain.model.user.UserProfile
import com.weit.domain.repository.place.PlaceReviewRepository
import com.weit.domain.repository.user.UserRepository
import java.time.LocalDateTime
import javax.inject.Inject

class GetPlaceReviewContentUseCase @Inject constructor(
    private val placeReviewRepository: PlaceReviewRepository,
    private val userRepository: UserRepository,
) {

    suspend operator fun invoke(
        size: Int,
        lastId: Long?,
        placeId: String
    ): Result<List<PlaceReviewInfo>> {
        val userId = userRepository.getUserId()
        val placeReviewResult =
            placeReviewRepository.getByPlaceId(
                PlaceReviewByPlaceIdQuery(
                    size = size,
                    lastPlaceReviewId = lastId,
                    placeId = placeId
                )
            )

        return if (placeReviewResult.isSuccess) {
            val review = placeReviewResult.getOrThrow()
            val list = review.map { it.toPlaceReviewInfo(userId) }

            Result.success(list)

        } else {
            Result.failure(placeReviewResult.exceptionOrNull() ?: UnKnownException())
        }
    }

    private fun PlaceReviewContent.toPlaceReviewInfo(myId: Long): PlaceReviewInfo =
        PlaceReviewInfo(
            writerNickname = userInfo.nickname,
            rating = (starRating.toFloat() / 2),
            review = review,
            createAt = createdAt,
            userId = userInfo.userId,
            isMine = userInfo.userId == myId,
            placeReviewId = placeReviewId,
            profile = userInfo.profile
        )
}
