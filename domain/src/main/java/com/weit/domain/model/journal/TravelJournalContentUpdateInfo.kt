package com.weit.domain.model.journal

data class TravelJournalContentUpdateInfo(
    val content: String?,
    val placeId: String?,
    val latitudes: List<Double>?,
    val longitudes: List<Double>?,
    val travelDate: String,
    val updateContentImageNames: List<String>?,
    val deleteContentImageIds: List<Long>?
)
