package com.weit.data.model.journal

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TravelCompanionSimpleResponsesDTO(
    @field:Json(name = "username") val username: String,
    @field:Json(name = "profileUrl") val profileUrl: String,
)
