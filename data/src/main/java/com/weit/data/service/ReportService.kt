package com.weit.data.service

import com.weit.data.model.report.CommunityReportRequestDTO
import com.weit.data.model.report.JournalReportRequestDTO
import com.weit.data.model.report.ReviewReportRequestDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ReportService {

    @POST("/api/v1/reports/place-review")
    suspend fun reviewReport(
        @Body reviewReportRequestDTO: ReviewReportRequestDTO
    ): Response<Unit>

    @POST("/api/v1/reports/travel-journal")
    suspend fun journalReport(
        @Body journalReportRequestDTO: JournalReportRequestDTO
    ): Response<Unit>

    @POST("/api/v1/reports/community")
    suspend fun communityReport(
        @Body communityReportRequestDTO: CommunityReportRequestDTO
    ): Response<Unit>
}
