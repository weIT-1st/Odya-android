package com.weit.data.model.reptraveljournal

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.weit.data.model.journal.TravelCompanionSimpleResponsesDTO
import com.weit.data.model.journal.TravelJournalWriterDTO

@JsonClass(generateAdapter = true)
data class RepTravelJournalListDTO(
    @field:Json(name = "repTravelJournalId") val repTravelJournalId: Long,
    @field:Json(name = "travelJournalId") val travelJournalId: Long,
    @field:Json(name = "title") val travelJournalTitle: String,
    @field:Json(name = "travelJournalMainImageUrl") val travelJournalMainImageUrl: String,
    @field:Json(name = "travelStartDate") val travelStartDate: String,
    @field:Json(name = "travelEndDate") val travelEndDate: String,
    @field:Json(name = "writer") val writer: TravelJournalWriterDTO,
    @field:Json(name = "travelCompanionSimpleResponses") val travelCompanionSimpleResponses: List<TravelCompanionSimpleResponsesDTO>,
)
