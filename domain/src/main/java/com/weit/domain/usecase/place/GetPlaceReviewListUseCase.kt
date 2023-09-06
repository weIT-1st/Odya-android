package com.weit.domain.usecase.place

import com.weit.domain.model.place.PlaceReviewByPlaceIdInfo
import com.weit.domain.model.place.PlaceReviewInfo
import com.weit.domain.model.user.UserByNicknameInfo
import com.weit.domain.usecase.user.GetUserByNicknameUseCase
import javax.inject.Inject

class GetPlaceReviewListUseCase @Inject constructor(
    private val getPlaceReviewByPlaceIdUseCase: GetPlaceReviewByPlaceIdUseCase,
    private val getUserByNicknameUseCase: GetUserByNicknameUseCase
) {

//    suspend operator fun invoke(placeId: String): Result<List<PlaceReviewInfo>> {
//        val result: List<PlaceReviewInfo> = emptyList<PlaceReviewInfo>()
//        val placeReviewResult =
//            getPlaceReviewByPlaceIdUseCase(PlaceReviewByPlaceIdInfo(placeId, 20))
//        if (placeReviewResult.isSuccess) {
//            val placeReview = placeReviewResult.getOrNull()
//            if (placeReview != null) {
//                for (item in placeReview) {
//                    val userResult = getUserByNicknameUseCase(
//                        UserByNicknameInfo(
//                            null,
//                            null,
//                            item.writerNickname
//                        )
//                    )
//                    if (userResult.isSuccess) {
//                        result.toMutableList().add(
//                            PlaceReviewInfo(
//                                item.writerNickname,
//                                item.starRating,
//                                item.review,
//                                item.createdAt,
//                                item.userId,
//                                userResult.getOrNull()?.get(0)!!.profile
//                            )
//                        )
//                    } else {
//                        return Result.failure(userResult.exceptionOrNull()!!)
//                    }
//                }
//            } else { return Result.success(result) }
//        } else { return Result.failure(placeReviewResult.exceptionOrNull()!!) }
//        return Result.success(result)
//    }
}
