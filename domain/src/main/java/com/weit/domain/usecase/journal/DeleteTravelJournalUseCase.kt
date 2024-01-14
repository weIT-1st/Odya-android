package com.weit.domain.usecase.journal

import com.weit.domain.repository.journal.TravelJournalRepository
import javax.inject.Inject

class DeleteTravelJournalUseCase @Inject constructor(
    private val repository: TravelJournalRepository
) {
    suspend operator fun invoke(travelJournalId : Long): Result<Unit> =
        repository.deleteTravelJournal(travelJournalId)
}
