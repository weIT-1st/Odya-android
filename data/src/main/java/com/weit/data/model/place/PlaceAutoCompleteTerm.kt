package com.weit.data.model.place

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PlaceAutoCompleteTerm(
    @field:Json(name = "offset") val offset: Int,
    @field:Json(name = "value") val value: String,
)
