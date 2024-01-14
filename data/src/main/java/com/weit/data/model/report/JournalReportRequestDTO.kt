package com.weit.data.model.report

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class JournalReportRequestDTO(
    @field:Json(name = "travelJournalId") val travelJournalId: Long,
    @field:Json(name = "reportReason") val reportReason: String,
    @field:Json(name = "otherReason") val otherReason: String?,
)
