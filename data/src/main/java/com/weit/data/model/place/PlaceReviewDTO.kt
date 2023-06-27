package com.weit.data.model.place

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PlaceReviewDTO(
    @field:Json(name = "id") val placeReviewId: Long,
    @field:Json(name = "placeId") val placeId: String,
    @field:Json(name = "userId") val userId: Long,
    @field:Json(name = "writerNickname") val writerNickname: String,
    @field:Json(name = "starRating") val starRating: Int,
    @field:Json(name = "review") val review: String,
)
