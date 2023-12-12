package com.weit.data.model.map

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PlaceDetailResponse(
    @field:Json(name = "html_attributions")val htmlAttributions: List<String>?,
    @field:Json(name = "result")val result: Place,
    @field:Json(name = "status")val status: String,
    @field:Json(name = "info_messages")val infoMessages: List<String>?,
)
