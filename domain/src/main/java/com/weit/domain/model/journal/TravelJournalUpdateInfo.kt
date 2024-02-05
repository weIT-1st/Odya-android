package com.weit.domain.model.journal

data class TravelJournalUpdateInfo(
    val title: String,
    val travelStartDate: List<Int>,
    val travelEndDate: List<Int>,
    val visibility: String,
    val travelCompanionIds: List<Long>?,
    val travelCompanionNames: List<String>?
)
