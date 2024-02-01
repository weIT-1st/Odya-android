package com.weit.domain.model.journal

data class TravelJournalRegistrationInfo(
    val title: String,
    val travelStartDate: List<Int>,
    val travelEndDate: List<Int>,
    val visibility: String,
    val travelCompanionIds: List<Long> = emptyList(),
    val travelCompanionNames: List<String> = emptyList(),
    val travelJournalContentRequests: List<TravelJournalContentRequest>,
    val travelDurationDays: Int,
    val contentImageNameTotalCount: Int
)
