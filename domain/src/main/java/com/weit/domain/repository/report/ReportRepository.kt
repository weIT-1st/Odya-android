package com.weit.domain.repository.report

import com.weit.domain.model.report.CommunityReportRequestInfo
import com.weit.domain.model.report.JournalReportRequestInfo
import com.weit.domain.model.report.ReviewReportRequestInfo

interface ReportRepository {
    suspend fun reviewReport(
        info: ReviewReportRequestInfo
    ): Result<Unit>

    suspend fun journalReport(
        info: JournalReportRequestInfo
    ): Result<Unit>

    suspend fun communityReport(
        info: CommunityReportRequestInfo
    ): Result<Unit>
}
