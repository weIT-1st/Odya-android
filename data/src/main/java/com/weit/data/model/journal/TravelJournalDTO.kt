package com.weit.data.model.journal

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TravelJournalDTO(
    @field:Json(name = "travelJournalId") val travelJournalId: Long,
    @field:Json(name = "title") val travelJournalTitle: String,
    @field:Json(name = "travelStartDate") val travelStartDate: String,
    @field:Json(name = "travelEndDate") val travelEndDate: String,
    @field:Json(name = "visibility") val visibility: String,
    @field:Json(name = "isBookmarked") val isBookmarked: Boolean,
    @field:Json(name = "isRepresentative") val isRepresentative: Boolean,
    @field:Json(name = "writer") val writer: TravelJournalWriterDTO,
    @field:Json(name = "travelJournalContents") val travelJournalContents: List<TravelJournalContentsDTO>,
    @field:Json(name = "travelJournalCompanions") val travelJournalCompanions: List<TravelJournalCompanionsDTO>,
)
