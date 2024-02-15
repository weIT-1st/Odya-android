package com.weit.domain.model.journal

data class TravelJournalInfo(
    val travelJournalId: Long,
    val travelJournalTitle: String,
    val travelStartDate: String,
    val travelEndDate: String,
    val visibility: String,
    val isBookmarked: Boolean,
    val isRepresentative: Boolean,
    val writer: TravelJournalWriterInfo,
    val travelJournalContents: List<TravelJournalContentsInfo>,
    val travelJournalCompanions: List<TravelJournalCompanionsInfo>,
)
