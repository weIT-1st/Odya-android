package com.weit.domain.model.report

data class CommunityReportRequestInfo(
    val communityId: Long,
    val reportReason: ReportReason,
    val otherReason: String?
)
