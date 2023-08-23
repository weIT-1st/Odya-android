package com.weit.data.model.place

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class IsExistPlaceReviewDTO(
    @field:Json(name = "exist") val isExist: Boolean
)
