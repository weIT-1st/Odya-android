package com.weit.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ListResponse<T>(
    @field:Json(name = "content") val content : List<T>,
    @field:Json(name = "hasNext") val hasNext: Boolean,
)