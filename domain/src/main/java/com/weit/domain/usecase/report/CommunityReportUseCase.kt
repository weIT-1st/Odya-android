package com.weit.domain.usecase.report

import com.weit.domain.model.report.CommunityReportRequestInfo
import com.weit.domain.repository.report.ReportRepository
import javax.inject.Inject

class CommunityReportUseCase @Inject constructor(
    private val repository: ReportRepository
) {
    suspend operator fun invoke(info: CommunityReportRequestInfo): Result<Unit> =
        repository.communityReport(info)
}
