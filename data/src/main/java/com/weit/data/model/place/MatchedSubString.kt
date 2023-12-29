package com.weit.data.model.place

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MatchedSubString(
    @field:Json(name = "length") val length: Int,
    @field:Json(name = "offset") val offset: Int,
)
