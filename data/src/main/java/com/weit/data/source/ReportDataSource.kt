package com.weit.data.source

import com.weit.data.model.report.CommunityReportRequestDTO
import com.weit.data.model.report.JournalReportRequestDTO
import com.weit.data.model.report.ReviewReportRequestDTO
import com.weit.data.service.ReportService
import retrofit2.Response
import javax.inject.Inject

class ReportDataSource @Inject constructor(
    private val service: ReportService
) {
    suspend fun reviewReport(reviewReportRequestDTO: ReviewReportRequestDTO): Response<Unit> =
        service.reviewReport(reviewReportRequestDTO)
    suspend fun journalReport(journalReportRequestDTO: JournalReportRequestDTO): Response<Unit> =
        service.journalReport(journalReportRequestDTO)
    suspend fun communityReport(communityReportRequestDTO: CommunityReportRequestDTO): Response<Unit> =
        service.communityReport(communityReportRequestDTO)
}
