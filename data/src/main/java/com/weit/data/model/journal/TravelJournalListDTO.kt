package com.weit.data.model.journal

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TravelJournalListDTO(
    @field:Json(name = "travelJournalId") val travelJournalId: Long,
    @field:Json(name = "title") val travelJournalTitle: String,
    @field:Json(name = "content") val testContent: String,
    @field:Json(name = "contentImageUrl") val contentImageUrl: String,
    @field:Json(name = "travelStartDate") val travelStartDate: String,
    @field:Json(name = "travelEndDate") val travelEndDate: String,
    @field:Json(name = "writer") val writer: TravelJournalWriterDTO,
    @field:Json(name = "travelCompanionSimpleResponses") val travelCompanionSimpleResponses: List<TravelCompanionSimpleResponsesDTO>,
    @field:Json(name = "placeIds") val placeIds: List<String>,
    @field:Json(name = "visibility") val visibility: String,
    @field:Json(name = "isBookmarked") val isBookmarked: Boolean
)
