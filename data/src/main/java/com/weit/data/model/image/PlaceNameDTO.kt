package com.weit.data.model.image

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PlaceNameDTO(
    @field:Json(name = "placeName") val placeName: String,
)
