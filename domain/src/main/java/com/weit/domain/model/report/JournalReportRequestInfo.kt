package com.weit.domain.model.report

data class JournalReportRequestInfo(
    val travelJournalId: Long,
    val reportReason: ReportReason,
    val otherReason: String?
)
