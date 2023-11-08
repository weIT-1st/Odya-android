package com.weit.domain.model.journal

data class TravelJournalContentsInfo(
    val travelJournalContentId: Long,
    val content: String,
    val placeId: String,
    val latitude: List<Double>,
    val longitude: List<Double>,
    val travelDate: String,
    val travelJournalContentImages: List<TravelJournalContentsImagesInfo>,
)
