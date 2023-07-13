package com.weit.data.model.place

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PlaceReviewModification(
    @field:Json(name = "id") val placeReviewId: Long,
    @field:Json(name = "rating") val rating: Int,
    @field:Json(name = "review") val review: String,
)
