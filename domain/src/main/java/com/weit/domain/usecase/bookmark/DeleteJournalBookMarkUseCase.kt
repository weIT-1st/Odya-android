package com.weit.domain.usecase.bookmark

import com.weit.domain.repository.bookmark.BookMarkRepository
import javax.inject.Inject

class DeleteJournalBookMarkUseCase @Inject constructor(
    private val repository: BookMarkRepository
) {
    suspend operator fun invoke(travelJournalId: Long): Result<Unit> =
        repository.deleteJournalBookMark(travelJournalId)
}
