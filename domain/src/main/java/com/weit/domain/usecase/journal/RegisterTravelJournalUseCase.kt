package com.weit.domain.usecase.journal

import com.weit.domain.model.journal.TravelJournalRegistrationInfo
import com.weit.domain.repository.journal.TravelJournalRepository
import javax.inject.Inject

class RegisterTravelJournalUseCase @Inject constructor(
    private val repository: TravelJournalRepository
) {
    suspend operator fun invoke(
        travelJournalRegistrationInfo: TravelJournalRegistrationInfo,
        travelJournalImages: List<String>
    ): Result<Unit> =
        repository.registerTravelJournal(travelJournalRegistrationInfo, travelJournalImages)
}
