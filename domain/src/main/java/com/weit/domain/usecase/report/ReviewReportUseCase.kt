package com.weit.domain.usecase.report

import com.weit.domain.model.report.ReviewReportRequestInfo
import com.weit.domain.repository.report.ReportRepository
import javax.inject.Inject

class ReviewReportUseCase @Inject constructor(
    private val repository: ReportRepository
) {
    suspend operator fun invoke(info: ReviewReportRequestInfo): Result<Unit> =
        repository.reviewReport(info)
}
