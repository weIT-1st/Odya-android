package com.weit.domain.model.bookmark


data class JournalBookMarkInfo(
    val travelJournalBookMarkId: Long,
    val travelJournalId: Long,
    val title: String,
    val travelStartDate: String,
    val travelJournalMainImageUrl: String,
    val writer: Writer,
)
