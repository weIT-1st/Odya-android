package com.weit.domain.model.report

data class ReviewReportRequestInfo(
    val placeReviewId: Long,
    val reportReason: ReportReason,
    val otherReason: String?
)
