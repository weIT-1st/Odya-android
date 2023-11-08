package com.weit.domain.model.journal

data class TravelJournalPlaceList(
    val travelJournalId: Long,
    val placeId: String,
    val latitude: Long,
    val longitude: Long,
    val travelJournalContentImage: TravelJournalContentsImagesInfo,
)
