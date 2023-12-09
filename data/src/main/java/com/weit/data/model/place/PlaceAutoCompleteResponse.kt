package com.weit.data.model.place

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PlaceAutoCompleteResponse(
    @field:Json(name = "predictions") val predictions: List<PlaceAutoCompletePrediction>,
    @field:Json(name = "status") val status: String,
    @field:Json(name = "error_message") val errorMessage: String?,
    @field:Json(name = "info_messages") val infoMessages: List<String>?,
)
