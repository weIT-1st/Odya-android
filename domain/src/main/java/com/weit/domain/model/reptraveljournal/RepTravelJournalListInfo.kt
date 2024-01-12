package com.weit.domain.model.reptraveljournal

import com.weit.domain.model.journal.TravelCompanionSimpleResponsesInfo
import com.weit.domain.model.journal.TravelJournalWriterInfo

data class RepTravelJournalListInfo(
    val repTravelJournalId: Long,
    val travelJournalId: Long,
    val title: String,
    val travelJournalMainImageUrl: String,
    val travelStartDate: String,
    val travelEndDate: String,
    val writer: TravelJournalWriterInfo,
    val travelCompanionSimpleResponses: List<TravelCompanionSimpleResponsesInfo>,
)
