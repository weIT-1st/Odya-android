package com.weit.domain.model.journal

import com.weit.domain.model.place.PlaceDetail

data class TravelJournalListInfo(
    val travelJournalId: Long,
    val travelJournalTitle: String,
    val content: String,
    val contentImageUrl: String,
    val travelStartDate: String,
    val travelEndDate: String,
    val placeDetail: List<PlaceDetail> = emptyList(),
    val writer: TravelJournalWriterInfo,
    val travelCompanionSimpleResponses: List<TravelCompanionSimpleResponsesInfo>,
)
