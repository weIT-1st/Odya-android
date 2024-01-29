package com.weit.domain.usecase.bookmark

import com.weit.domain.model.bookmark.JournalBookMarkInfo
import com.weit.domain.repository.bookmark.BookMarkRepository
import javax.inject.Inject

class GetMyJournalBookMarkUseCase @Inject constructor(
    private val repository: BookMarkRepository
) {
    suspend operator fun invoke(size: Int?, lastId: Long?, sortType: String?
    ): Result<List<JournalBookMarkInfo>> =
        repository.getMyJournalBookMark(size, lastId, sortType)
}
