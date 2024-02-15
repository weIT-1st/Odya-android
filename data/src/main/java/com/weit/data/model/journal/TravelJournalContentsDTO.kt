package com.weit.data.model.journal

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TravelJournalContentsDTO(
    @field:Json(name = "travelJournalContentId") val travelJournalContentId: Long,
    @field:Json(name = "content") val content: String,
    @field:Json(name = "placeId") val placeId: String?,
    @field:Json(name = "latitudes") val latitude: List<Double>,
    @field:Json(name = "longitudes") val longitude: List<Double>,
    @field:Json(name = "travelDate") val travelDate: String,
    @field:Json(name = "travelJournalContentImages") val travelJournalContentImages: List<TravelJournalContentsImagesDTO>,
)
