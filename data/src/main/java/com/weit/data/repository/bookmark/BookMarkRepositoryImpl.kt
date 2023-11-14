package com.weit.data.repository.bookmark

import android.content.res.Resources.NotFoundException
import com.weit.data.source.BookMarkDataSource
import com.weit.domain.model.bookmark.JournalBookMarkInfo
import com.weit.domain.model.exception.InvalidTokenException
import com.weit.domain.model.exception.RequestResourceAlreadyExistsException
import com.weit.domain.model.exception.UnKnownException
import com.weit.domain.repository.bookmark.BookMarkRepository
import okhttp3.internal.http.HTTP_CONFLICT
import okhttp3.internal.http.HTTP_NOT_FOUND
import okhttp3.internal.http.HTTP_UNAUTHORIZED
import retrofit2.HttpException
import retrofit2.Response
import javax.inject.Inject

class BookMarkRepositoryImpl @Inject constructor(
    private val dataSource: BookMarkDataSource
): BookMarkRepository {
    override suspend fun createJournalBookmark(travelJournalId: Long): Result<Unit> =
        handleBookMark(dataSource.createJournalBookMark(travelJournalId))

//    override suspend fun getMyJournalBookMark(
//        size: Int?,
//        lastId: Long?,
//        sortType: String?
//    ): Result<List<JournalBookMarkInfo>> {
//        TODO("Not yet implemented")
//    }

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
