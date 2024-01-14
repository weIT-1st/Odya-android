package com.weit.domain.model.journal

data class TravelJournalListInfo(
    val travelJournalId: Long,
    val travelJournalTitle: String,
    val content: String,
    val contentImageUrl: String,
    val travelStartDate: String,
    val travelEndDate: String,
    val placeIds: List<String>,
    val writer: TravelJournalWriterInfo,
    val travelCompanionSimpleResponses: List<TravelCompanionSimpleResponsesInfo>,
    val visibility: String,
    val isBookmarked: Boolean
)
