package com.weit.data.repository.journal

import android.content.res.Resources.NotFoundException
import com.weit.data.model.ListResponse
import com.weit.data.model.journal.TravelJournalListDTO
import com.weit.data.source.TravelJournalDataSource
import com.weit.data.util.exception
import com.weit.domain.model.exception.ForbiddenException
import com.weit.domain.model.exception.InvalidRequestException
import com.weit.domain.model.exception.InvalidTokenException
import com.weit.domain.model.exception.NoMoreItemException
import com.weit.domain.model.exception.UnKnownException
import com.weit.domain.model.journal.TravelCompanionSimpleResponsesInfo
import com.weit.domain.model.journal.TravelJournalCompanionsInfo
import com.weit.domain.model.journal.TravelJournalContentsImagesInfo
import com.weit.domain.model.journal.TravelJournalContentsInfo
import com.weit.domain.model.journal.TravelJournalInfo
import com.weit.domain.model.journal.TravelJournalListInfo
import com.weit.domain.model.journal.TravelJournalWriterInfo
import com.weit.domain.repository.journal.TravelJournalRepository
import okhttp3.internal.http.HTTP_BAD_REQUEST
import okhttp3.internal.http.HTTP_FORBIDDEN
import okhttp3.internal.http.HTTP_NOT_FOUND
import okhttp3.internal.http.HTTP_UNAUTHORIZED
import retrofit2.HttpException
import retrofit2.Response
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

class TravelJournalRepositoryImpl @Inject constructor(
    private val dataSource: TravelJournalDataSource
) : TravelJournalRepository {

    private val hasNextJournal = AtomicBoolean(true)
    private val hasNextMyJournal = AtomicBoolean(true)
    private val hasNextFriendJournal = AtomicBoolean(true)
    private val hasNextRecommendJournal = AtomicBoolean(true)
    private val hasNextTaggedJournal = AtomicBoolean(true)

    // 여행일지 생성 API

    override suspend fun getTravelJournal(travelJournalId: Long): Result<TravelJournalInfo> {
        val result = runCatching { dataSource.getTravelJournal(travelJournalId) }

        return if (result.isSuccess) {
            val journal = result.getOrThrow()
            Result.success(
                TravelJournalInfo(
                    journal.travelJournalId,
                    journal.travelJournalTitle,
                    journal.travelStartDate,
                    journal.travelEndDate,
                    journal.visibility,
                    journal.isBookmarked,
                    TravelJournalWriterInfo(
                        journal.writer.userId,
                        journal.writer.nickname,
                        journal.writer.profile
                    ),
                    journal.travelJournalContents.map { content ->
                        TravelJournalContentsInfo(
                            content.travelJournalContentId,
                            content.content,
                            content.placeId,
                            content.latitude,
                            content.longitude,
                            content.travelDate,
                            content.travelJournalContentImages.map { image ->
                                TravelJournalContentsImagesInfo(
                                    image.travelJournalContentImageId,
                                    image.contentImageName,
                                    image.contentImageUrl
                                )
                            }
                        )
                    },
                    journal.travelJournalCompanions.map { companion ->
                        TravelJournalCompanionsInfo(
                        companion.userId,
                        companion.nickname,
                        companion.isRegistered
                        )
                    }
                )
            )
        } else {
            Result.failure(handleJournalError(result.exception()))
        }
    }

    override suspend fun getTravelJournalList(
        size: Int?,
        lastTravelJournal: Long?
    ): Result<List<TravelJournalListInfo>> =
        getInfiniteJournalList(
            hasNextJournal,
            runCatching { dataSource.getTravelJournalList(size, lastTravelJournal) }
        )

    override suspend fun getMyTravelJournalList(
        size: Int?,
        lastTravelJournal: Long?
    ): Result<List<TravelJournalListInfo>> =
        getInfiniteJournalList(
            hasNextMyJournal,
            runCatching { dataSource.getMyTravelJournalList(size, lastTravelJournal) }
        )

    override suspend fun getFriendTravelJournalList(
        size: Int?,
        lastTravelJournal: Long?
    ): Result<List<TravelJournalListInfo>> =
        getInfiniteJournalList(
            hasNextFriendJournal,
            runCatching { dataSource.getFriendTravelJournalList(size, lastTravelJournal) }
        )

    override suspend fun getRecommendTravelJournalList(
        size: Int?,
        lastTravelJournal: Long?
    ): Result<List<TravelJournalListInfo>> =
        getInfiniteJournalList(
            hasNextRecommendJournal,
            runCatching { dataSource.getRecommendTravelJournalList(size, lastTravelJournal) }
        )

    override suspend fun getTaggedTravelJournalList(
        size: Int?,
        lastTravelJournal: Long?
    ): Result<List<TravelJournalListInfo>> =
        getInfiniteJournalList(
            hasNextTaggedJournal,
            runCatching { dataSource.getRecommendTravelJournalList(size, lastTravelJournal) }
        )

    // 여행일지 수정 Api

    // 여행일지 콘텐츠 수정 Api

    override suspend fun deleteTravelJournal(travelJournalId: Long): Result<Unit> =
        delete(dataSource.deleteTravelJournal(travelJournalId))

    override suspend fun deleteTravelJournalContent(
        travelJournalId: Long,
        travelJournalContentId: Long
    ): Result<Unit> =
        delete(dataSource.deleteTravelJournalContent(travelJournalId, travelJournalContentId))

    override suspend fun deleteTravelJournalFriend(travelJournalId: Long): Result<Unit> =
        delete(dataSource.deleteTravelJournalFriend(travelJournalId))

    private fun getInfiniteJournalList(
        hasNest: AtomicBoolean,
        result: Result<ListResponse<TravelJournalListDTO>>
    ): Result<List<TravelJournalListInfo>> {
        if (hasNest.get().not()) {
            return Result.failure(NoMoreItemException())
        }

        return if (result.isSuccess) {
            val listSearch = result.getOrThrow()
            hasNest.set(listSearch.hasNext)
            Result.success(listSearch.content.map {
                TravelJournalListInfo(
                    it.travelJournalId,
                    it.travelJournalTitle,
                    it.testContent,
                    it.contentImageUrl,
                    it.travelStartDate,
                    it.travelEndDate,
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

    private fun delete(response: Response<Unit>): Result<Unit> =
        if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(handleJournalError((handleResponseError(response))))
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
