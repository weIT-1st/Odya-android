package com.weit.data.model.place

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PlaceReviewListDTO(
    @field:Json(name = "content") val reviews: List<PlaceReviewDTO>,
    @field:Json(name = "hasNext") val hasNext: Boolean,
)
