package com.weit.data.repository.bookmark

import android.content.res.Resources.NotFoundException
import com.orhanobut.logger.Logger
import com.weit.data.source.BookMarkDataSource
import com.weit.data.util.exception
import com.weit.domain.model.bookmark.JournalBookMarkInfo
import com.weit.domain.model.bookmark.Writer
import com.weit.domain.model.exception.InvalidTokenException
import com.weit.domain.model.exception.NoMoreItemException
import com.weit.domain.model.exception.RequestResourceAlreadyExistsException
import com.weit.domain.model.exception.UnKnownException
import com.weit.domain.repository.bookmark.BookMarkRepository
import okhttp3.internal.http.HTTP_CONFLICT
import okhttp3.internal.http.HTTP_NOT_FOUND
import okhttp3.internal.http.HTTP_UNAUTHORIZED
import retrofit2.HttpException
import retrofit2.Response
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

class BookMarkRepositoryImpl @Inject constructor(
    private val dataSource: BookMarkDataSource
): BookMarkRepository {

    private val hasNextMyBookMark = AtomicBoolean(true)
    private val hasNextUserBookMark = AtomicBoolean(true)
    override suspend fun createJournalBookmark(travelJournalId: Long): Result<Unit> =
        handleBookMark(dataSource.createJournalBookMark(travelJournalId))

    override suspend fun getMyJournalBookMark(
        size: Int?,
        lastId: Long?,
        sortType: String?
    ): Result<List<JournalBookMarkInfo>> {

        if(lastId == null){
            hasNextMyBookMark.set(true)
        }

        if (hasNextMyBookMark.get().not()){
            return Result.failure(NoMoreItemException())
        }
        val result = runCatching {
            dataSource.getMyJournalBookMark(
                size,
                lastId,
                sortType
            )
        }

        return if (result.isSuccess){
            val myJournals = result.getOrThrow()
            hasNextMyBookMark.set(myJournals.hasNext)
            Result.success(myJournals.content.map {
                JournalBookMarkInfo(
                    it.travelJournalBookmarkId,
                    it.travelJournalId,
                    it.title,
                    it.travelStartDate,
                    it.travelJournalMainImageUrl,
                    Writer(
                        it.writer.userId,
                        it.writer.nickname,
                        it.writer.profile,
                        it.writer.isFollowing
                    ),
                    it.isBookmarked
                )
            })
        } else {
            Logger.t("MainTest").i("${result.exceptionOrNull()}")

            Result.failure(handleBookMarkError(result.exception()))
        }
    }

    override suspend fun getUserJournalBookmark(
        userId: Long,
        size: Int?,
        lastId: Long?,
        sortType: String?
    ): Result<List<JournalBookMarkInfo>> {
        if(lastId == null){
            hasNextUserBookMark.set(true)
        }
        if (hasNextUserBookMark.get().not()){
            return Result.failure(NoMoreItemException())
        }
        val result = runCatching {
            dataSource.getUserJournalBookmark(
                userId,
                size,
                lastId,
                sortType
            )
        }

        return if (result.isSuccess){
            val userJournal = result.getOrThrow()
            hasNextUserBookMark.set(userJournal.hasNext)
            Result.success(userJournal.content.map{
                JournalBookMarkInfo(
                    it.travelJournalBookmarkId,
                    it.travelJournalId,
                    it.title,
                    it.travelStartDate,
                    it.travelJournalMainImageUrl,
                    Writer(
                        it.writer.userId,
                        it.writer.nickname,
                        it.writer.profile,
                        it.writer.isFollowing
                    ),
                    it.isBookmarked
                )
            })
        } else {
            Result.failure(handleBookMarkError(result.exception()))
        }

    }

    override suspend fun deleteJournalBookMark(travelJournalId: Long): Result<Unit> =
        handleBookMark(dataSource.deleteJournalBookMark(travelJournalId))

    private fun handleBookMark(response: Response<Unit>) =
        if (response.isSuccessful){
            Result.success(Unit)
        } else {
            Result.failure(handleBookMarkError(handleResponseError(response)))
        }

    private fun handleBookMarkError(t: Throwable): Throwable =
        if (t is HttpException){
            handleCode(t.code())
        } else {
            t
        }

    private fun handleCode(code: Int): Throwable =
        when(code){
            HTTP_NOT_FOUND -> NotFoundException()
            HTTP_CONFLICT -> RequestResourceAlreadyExistsException()
            HTTP_UNAUTHORIZED -> InvalidTokenException()
            else -> UnKnownException()
        }

    private fun handleResponseError(response: Response<*>): Throwable =
        handleCode(response.code())
}
