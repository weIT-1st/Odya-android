package com.weit.data.model.place

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AverageRatingDTO(
    @field:Json(name = "averageStarRating") val averageRating: Float,
)
