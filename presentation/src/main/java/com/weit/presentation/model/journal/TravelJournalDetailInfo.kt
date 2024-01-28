package com.weit.presentation.model.journal

import com.weit.domain.model.journal.TravelJournalContentsImagesInfo

data class TravelJournalDetailInfo(
    val travelJournalContentId: Long,
    val travelDate : String,
    val content : String,
    val placeName : String?,
    val placeAddress : String?,
    val travelJournalContentImages : List<TravelJournalContentsImagesInfo>
)
