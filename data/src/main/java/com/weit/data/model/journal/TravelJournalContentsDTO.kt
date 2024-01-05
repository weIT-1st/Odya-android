package com.weit.data.model.journal

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TravelJournalContentsDTO(
    @field:Json(name = "travelJournalContentId") val travelJournalContentId: Long,
    @field:Json(name = "content") val content: String,
    @field:Json(name = "placeId") val placeId: String?,
    @field:Json(name = "travelDate") val travelDate: String,
    @field:Json(name = "latitude") val latitude: List<Double> = emptyList(),
    @field:Json(name = "longitude") val longitude: List<Double> = emptyList(),
    @field:Json(name = "travelJournalContentImages") val travelJournalContentImages: List<TravelJournalContentsImagesDTO>,
)
