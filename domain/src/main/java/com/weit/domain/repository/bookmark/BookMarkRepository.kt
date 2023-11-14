package com.weit.domain.repository.bookmark

import com.weit.domain.model.bookmark.JournalBookMarkInfo

interface BookMarkRepository {
    suspend fun createJournalBookmark(travelJournalId: Long): Result<Unit>

//    suspend fun getMyJournalBookMark(
//        size: Int?,
//        lastId: Long?,
//        sortType: String?
//    ): Result<List<JournalBookMarkInfo>>

    suspend fun deleteJournalBookMark(travelJournalId: Long): Result<Unit>
}
