package com.weit.domain.usecase.journal

import com.weit.domain.repository.journal.TravelJournalRepository
import javax.inject.Inject

class DeleteTravelJournalContentUseCase @Inject constructor(
    private val repository: TravelJournalRepository
) {
    suspend operator fun invoke(travelJournalId: Long, travelJournalContentId: Long): Result<Unit> =
        repository.deleteTravelJournalContent(travelJournalId, travelJournalContentId)
}
