package com.weit.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WeitUserDTO(
    @field:Json(name = "hashValue") val hash: Int,
    @field:Json(name = "originalName") val name: String,
)
