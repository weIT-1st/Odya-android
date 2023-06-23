package com.weit.data.model.map

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GeocodingResult(
    @field:Json(name = "html_attributions")val htmlAttributions: Array<String>?,
    @field:Json(name = "results")val result: Array<Place>,
    @field:Json(name = "status")val status: String,
    @field:Json(name = "info_messages")val info_messages: Array<String>?,

)
