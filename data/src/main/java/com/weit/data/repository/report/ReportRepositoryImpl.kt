package com.weit.data.repository.report

import android.content.res.Resources.NotFoundException
import com.weit.data.model.report.ReviewReportRequestDTO
import com.weit.data.source.ReportDataSource
import com.weit.domain.model.exception.InvalidRequestException
import com.weit.domain.model.exception.InvalidTokenException
import com.weit.domain.model.exception.RequestResourceAlreadyExistsException
import com.weit.domain.model.exception.UnKnownException
import com.weit.domain.model.report.CommunityReportRequestInfo
import com.weit.domain.model.report.JournalReportRequestInfo
import com.weit.domain.model.report.ReportReason
import com.weit.domain.model.report.ReviewReportRequestInfo
import com.weit.domain.repository.report.ReportRepository
import okhttp3.internal.http.HTTP_BAD_REQUEST
import okhttp3.internal.http.HTTP_CONFLICT
import okhttp3.internal.http.HTTP_NOT_FOUND
import okhttp3.internal.http.HTTP_UNAUTHORIZED
import retrofit2.HttpException
import retrofit2.Response
import javax.annotation.meta.When
import javax.inject.Inject

class ReportRepositoryImpl @Inject constructor(
    private val dataSource: ReportDataSource
): ReportRepository {
    override suspend fun reviewReport(info: ReviewReportRequestInfo): Result<Unit> =
        report(dataSource.reviewReport(
            ReviewReportRequestDTO(
                info.placeReviewId,
                handleReportReason(info.reportReason),
                info.otherReason
            )))

    override suspend fun journalReport(info: JournalReportRequestInfo): Result<Unit> =
        report(dataSource.reviewReport(
            ReviewReportRequestDTO(
                info.travelJournalId,
                handleReportReason(info.reportReason),
                info.otherReason
            )))

    override suspend fun communityReport(info: CommunityReportRequestInfo): Result<Unit> =
        report(dataSource.reviewReport(
            ReviewReportRequestDTO(
                info.communityId,
                handleReportReason(info.reportReason),
                info.otherReason
            )))

    private fun report(response: Response<Unit>): Result<Unit> =
        if (response.isSuccessful){
            Result.success(Unit)
        } else {
            Result.failure(handleReportError(handleResponseError(response)))
        }

    private fun handleReportReason(reportReason: ReportReason): String =
        when(reportReason){
            ReportReason.SPAM -> "SPAM"
            ReportReason.PORNOGRAPHY -> "PORNOGRAPHY"
            ReportReason.SWEAR_WORD -> "SWEAR_WORD"
            ReportReason.OVER_POST -> "OVER_POST"
            ReportReason.INFO_LEAK -> "INFO_LEAK"
            ReportReason.OTHER -> "OTHER"
         }

    private fun handleReportError(t: Throwable): Throwable =
        if (t is HttpException){
            handleCode(t.code())
        } else {
            t
        }

    private fun handleCode(code: Int): Throwable =
        when(code){
            HTTP_BAD_REQUEST -> InvalidRequestException()
            HTTP_NOT_FOUND -> NotFoundException()
            HTTP_CONFLICT -> RequestResourceAlreadyExistsException()
            HTTP_UNAUTHORIZED -> InvalidTokenException()
            else -> UnKnownException()
        }

    private fun handleResponseError(response: Response<*>): Throwable =
        handleCode(response.code())
}
