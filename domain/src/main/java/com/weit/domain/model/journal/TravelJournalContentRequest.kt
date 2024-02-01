package com.weit.domain.model.journal

data class TravelJournalContentRequest(
    val content: String? = "",
    val placeId: String?,
    val latitudes: List<Double>?,
    val longitudes: List<Double>?,
    val travelDate: List<Int>,
    val contentImageNames: List<String>,
)
