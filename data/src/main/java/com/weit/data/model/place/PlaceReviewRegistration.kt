package com.weit.data.model.place

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PlaceReviewRegistration(
    @field:Json(name = "placeId") val placeId: String,
    @field:Json(name = "rating") val rating: Int,
    @field:Json(name = "review") val review: String,
)
