package com.weit.data.model.place

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PlaceReviewListDTO(
    @field:Json(name = "reviews") val reviews: List<PlaceReviewDTO>,
    @field:Json(name = "lastId") val lastId: Long,
    @field:Json(name = "isLast") val isLast: Boolean,
)
