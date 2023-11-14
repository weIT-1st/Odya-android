package com.weit.domain.usecase.report

import com.weit.domain.model.report.JournalReportRequestInfo
import com.weit.domain.repository.report.ReportRepository
import javax.inject.Inject

class JournalReportUseCase @Inject constructor(
    private val repository: ReportRepository
) {
    suspend operator fun invoke(info: JournalReportRequestInfo): Result<Unit> =
        repository.journalReport(info)
}
