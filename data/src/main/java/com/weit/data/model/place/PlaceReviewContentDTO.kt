package com.weit.data.model.place

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PlaceReviewContentDTO(
    @field:Json(name = "id") val placeReviewId: Long,
    @field:Json(name = "placeId") val placeId: String,
    @field:Json(name = "userInfo") val userInfo: UserInfoDTO,
    @field:Json(name = "starRating") val starRating: Int,
    @field:Json(name = "review") val review: String,
    @field:Json(name = "createdAt") val createdAt: String,
)
