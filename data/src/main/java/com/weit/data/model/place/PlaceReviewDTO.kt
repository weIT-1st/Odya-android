package com.weit.data.model.place

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PlaceReviewDTO(
    @field:Json(name = "hasNext") val hasNext: Boolean,
    @field:Json(name = "averageRating") val averageRating: Float,
    @field:Json(name = "content") val reviews: List<PlaceReviewContentDTO>,
)
