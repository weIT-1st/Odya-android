package com.weit.data.model.report

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ReviewReportRequestDTO(
    @field:Json(name = "placeReviewId") val placeReviewId: Long,
    @field:Json(name = "reportReason") val reportReason: String,
    @field:Json(name = "otherReason") val otherReason: String?,
)
