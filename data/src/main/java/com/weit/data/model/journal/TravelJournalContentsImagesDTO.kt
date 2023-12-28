package com.weit.data.model.journal

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TravelJournalContentsImagesDTO(
    @field:Json(name = "travelJournalContentImageId") val travelJournalContentImageId: Long,
    @field:Json(name = "contentImageName") val contentImageName: String,
    @field:Json(name = "contentImageUrl") val contentImageUrl: String,
)
