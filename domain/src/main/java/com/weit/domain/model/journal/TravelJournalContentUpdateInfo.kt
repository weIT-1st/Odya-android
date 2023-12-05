package com.weit.domain.model.journal

import java.time.LocalDate

data class TravelJournalContentUpdateInfo(
    val content: String?,
    val placeId: String?,
    val latitudes: List<Double>?,
    val longitudes: List<Double>,
    val travelDate: LocalDate,
    val updateContentImageNames: List<String>?,
    val deleteContentImageIds: List<Long>?
)
