package com.weit.domain.model.journal

data class TravelJournalUpdateInfo(
    val title: String,
    val travelStartDate: String,
    val travelEndDate: String,
    val visibility: String,
    val travelCompanionIds: List<Long>?,
    val travelCompanionNames: List<String>?
)
