package com.weit.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WeitUserName(
    @field:Json(name = "name") val name: String,
)
