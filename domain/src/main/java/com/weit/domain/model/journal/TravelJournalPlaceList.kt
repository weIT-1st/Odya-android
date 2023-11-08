package com.weit.domain.model.journal

data class TravelJournalPlaceList(
    val travelJournalId: Long,
    val placeId: String,
    val latitude: Double,
    val longitude: Double,
    val travelJournalContentImage: TravelJournalContentsImagesInfo,
)
