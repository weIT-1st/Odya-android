package com.weit.domain.usecase.journal

import com.weit.domain.model.journal.TravelJournalInfo
import com.weit.domain.repository.journal.TravelJournalRepository
import javax.inject.Inject

class GetTravelJournalUseCase @Inject constructor(
    private val repository: TravelJournalRepository
) {
    suspend operator fun invoke(travelJournalId : Long): Result<TravelJournalInfo> =
        repository.getTravelJournal(travelJournalId)
}
