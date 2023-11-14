package com.weit.data.model.bookmark

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class JournalBookMarkDTO(
    @field:Json(name = "travelJournalBookMarkId") val travelJournalBookMarkId: Long,
    @field:Json(name = "travelJournalId") val travelJournalId: Long,
    @field:Json(name = "title") val title: String,
    @field:Json(name = "travelStartDate") val travelStartDate: String,
    @field:Json(name = "travelJournalMainImageUrl") val travelJournalMainImageUrl: String,
//    @field:Json(name = "writer") val writer: Writer,
)
