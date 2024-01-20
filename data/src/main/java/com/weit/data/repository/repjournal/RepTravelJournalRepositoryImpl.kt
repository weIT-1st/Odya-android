package com.weit.data.repository.repjournal

import android.content.res.Resources.NotFoundException
import com.squareup.moshi.Moshi
import com.weit.data.model.ListResponse
import com.weit.data.model.journal.TravelJournalCompanionsDTO
import com.weit.data.model.journal.TravelJournalContentsDTO
import com.weit.data.model.journal.TravelJournalContentsImagesDTO
import com.weit.data.model.journal.TravelJournalDTO
import com.weit.data.model.journal.TravelJournalListDTO
import com.weit.data.model.journal.TravelJournalVisibility
import com.weit.data.model.journal.TravelJournalWriterDTO
import com.weit.data.repository.image.ImageRepositoryImpl
import com.weit.data.source.ImageDataSource
import com.weit.data.source.RepTravelJournalDataSource
import com.weit.data.source.TravelJournalDataSource
import com.weit.data.util.exception
import com.weit.domain.model.exception.ForbiddenException
import com.weit.domain.model.exception.InvalidRequestException
import com.weit.domain.model.exception.InvalidTokenException
import com.weit.domain.model.exception.NoMoreItemException
import com.weit.domain.model.exception.UnKnownException
import com.weit.domain.model.journal.TravelCompanionSimpleResponsesInfo
import com.weit.domain.model.journal.TravelJournalCompanionsInfo
import com.weit.domain.model.journal.TravelJournalContentUpdateInfo
import com.weit.domain.model.journal.TravelJournalContentsImagesInfo
import com.weit.domain.model.journal.TravelJournalContentsInfo
import com.weit.domain.model.journal.TravelJournalInfo
import com.weit.domain.model.journal.TravelJournalListInfo
import com.weit.domain.model.journal.TravelJournalRegistrationInfo
import com.weit.domain.model.journal.TravelJournalUpdateInfo
import com.weit.domain.model.journal.TravelJournalVisibilityInfo
import com.weit.domain.model.journal.TravelJournalWriterInfo
import com.weit.domain.model.reptraveljournal.RepTravelJournalListInfo
import com.weit.domain.model.reptraveljournal.RepTravelJournalRequest
import com.weit.domain.repository.image.ImageRepository
import com.weit.domain.repository.journal.TravelJournalRepository
import com.weit.domain.repository.repjournal.RepTravelJournalRepository
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.internal.http.HTTP_BAD_REQUEST
import okhttp3.internal.http.HTTP_FORBIDDEN
import okhttp3.internal.http.HTTP_NOT_FOUND
import okhttp3.internal.http.HTTP_UNAUTHORIZED
import retrofit2.HttpException
import retrofit2.Response
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

class RepTravelJournalRepositoryImpl @Inject constructor(
    private val repTravelJournalDataSource: RepTravelJournalDataSource,
) : RepTravelJournalRepository {

    private val hasNextMyJournal = AtomicBoolean(true)
    private val hasNextOtherJournal = AtomicBoolean(true)

    override suspend fun registerRepTravelJournal(
        travelJournalId: Long,
    ): Result<Unit> {
        val result = runCatching {
            repTravelJournalDataSource.registerRepTravelJournal(travelJournalId)
        }
        return if (result.isSuccess) {
            Result.success(result.getOrThrow())
        } else {
            Result.failure(handleJournalError(result.exception()))
        }
    }

    override suspend fun getMyRepTravelJournalList(
        repTravelJournalRequest: RepTravelJournalRequest
    ): Result<List<RepTravelJournalListInfo>> {

        if (repTravelJournalRequest.lastRepTravelJournalId == null) {
            hasNextMyJournal.set(true)
        }

        if (hasNextMyJournal.get().not()) {
            return Result.failure(NoMoreItemException())
        }

        val result = runCatching {
            repTravelJournalDataSource.getMyRepTravelJournalList(
                repTravelJournalRequest.size,
                repTravelJournalRequest.lastRepTravelJournalId,
                repTravelJournalRequest.sortType
            )
        }

        return if (result.isSuccess) {
            val listSearch = result.getOrThrow()
            hasNextMyJournal.set(listSearch.hasNext)
            Result.success(listSearch.content.map {
                RepTravelJournalListInfo(
                    it.repTravelJournalId,
                    it.travelJournalId,
                    it.travelJournalTitle,
                    it.travelJournalMainImageUrl,
                    it.travelEndDate,
                    it.travelStartDate,
                    TravelJournalWriterInfo(
                        it.writer.userId,
                        it.writer.nickname,
                        it.writer.profile
                    ),
                    it.travelCompanionSimpleResponses.map { response ->
                        TravelCompanionSimpleResponsesInfo(
                            response.username,
                            response.profileUrl
                        )
                    }
                )
            })
        } else {
            Result.failure(handleJournalError(result.exception()))
        }
    }

    override suspend fun getOtherRepTravelJournalList(
        repTravelJournalRequest: RepTravelJournalRequest,
        userId: Long
    ): Result<List<RepTravelJournalListInfo>> {
        if (repTravelJournalRequest.lastRepTravelJournalId == null) {
            hasNextMyJournal.set(true)
        }

        if (hasNextMyJournal.get().not()) {
            return Result.failure(NoMoreItemException())
        }

        val result = runCatching {
            repTravelJournalDataSource.getOtherRepTravelJournalList(
                userId,
                repTravelJournalRequest.size,
                repTravelJournalRequest.lastRepTravelJournalId,
                repTravelJournalRequest.sortType
            )
        }

        return if (result.isSuccess) {
            val listSearch = result.getOrThrow()
            hasNextMyJournal.set(listSearch.hasNext)
            Result.success(listSearch.content.map {
                RepTravelJournalListInfo(
                    it.repTravelJournalId,
                    it.travelJournalId,
                    it.travelJournalTitle,
                    it.travelJournalMainImageUrl,
                    it.travelEndDate,
                    it.travelStartDate,
                    TravelJournalWriterInfo(
                        it.writer.userId,
                        it.writer.nickname,
                        it.writer.profile
                    ),
                    it.travelCompanionSimpleResponses.map { response ->
                        TravelCompanionSimpleResponsesInfo(
                            response.username,
                            response.profileUrl
                        )
                    }
                )
            })
        } else {
            Result.failure(handleJournalError(result.exception()))
        }
    }

    override suspend fun deleteRepTravelJournal(repTravelJournalId: Long): Result<Unit> {
        val response = repTravelJournalDataSource.deleteRepTravelJournal(repTravelJournalId)
        return if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(handleJournalError((handleResponseError(response))))
        }
    }


    private fun handleResponseError(response: Response<*>): Throwable {
        return handleCode(response.code())
    }

    private fun handleJournalError(t: Throwable): Throwable {
        return if (t is HttpException) {
            handleCode(t.code())
        } else {
            t
        }
    }

    private fun handleCode(code: Int): Throwable {
        return when (code) {
            HTTP_FORBIDDEN -> ForbiddenException()  // 403
            HTTP_UNAUTHORIZED -> InvalidTokenException()// 401
            HTTP_BAD_REQUEST -> InvalidRequestException() // 400
            HTTP_NOT_FOUND -> NotFoundException() // 404
            else -> UnKnownException()
        }
    }
}
