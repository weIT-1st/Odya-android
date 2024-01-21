package com.weit.domain.model.journal

import com.weit.domain.model.place.PlaceDetail

data class TravelJournalContentsInfo(
    val travelJournalContentId: Long,
    val content: String,
    val placeDetail: PlaceDetail,
    val latitude: List<Double> = emptyList(),
    val longitude: List<Double> = emptyList(),
    val travelDate: String,
    val travelJournalContentImages: List<TravelJournalContentsImagesInfo>,
)
