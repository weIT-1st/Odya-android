package com.weit.data.source

import com.weit.data.model.ListResponse
import com.weit.data.model.bookmark.JournalBookMarkDTO
import com.weit.data.service.BookMarkService
import retrofit2.Response
import javax.inject.Inject

class BookMarkDataSource @Inject constructor(
    private val service: BookMarkService
) {
    suspend fun createJournalBookMark(travelJournalId: Long): Response<Unit> =
        service.createJournalBookMark(travelJournalId)

    suspend fun getMyJournalBookMark(
        size: Int?,
        lastId: Long?,
        sortType: String?
    ): ListResponse<JournalBookMarkDTO> =
        service.getMyJournalBookMark(size, lastId, sortType)

    suspend fun deleteJournalBookMark(travelJournalId: Long): Response<Unit> =
        service.deleteJournalBookMark(travelJournalId)
}
