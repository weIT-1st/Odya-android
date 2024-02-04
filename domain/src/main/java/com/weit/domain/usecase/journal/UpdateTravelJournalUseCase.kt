package com.weit.domain.usecase.journal

import com.weit.domain.model.journal.TravelJournalUpdateInfo
import com.weit.domain.repository.journal.TravelJournalRepository
import javax.inject.Inject

class UpdateTravelJournalUseCase @Inject constructor(
    private val repository: TravelJournalRepository
) {
    suspend operator fun invoke(
        travelJournalId: Long,
        travelJournalUpdateInfo: TravelJournalUpdateInfo,
    ): Result<Unit> =
        repository.updateTravelJournal(travelJournalId, travelJournalUpdateInfo)
}
