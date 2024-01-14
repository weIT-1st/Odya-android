package com.weit.domain.usecase.journal

import com.weit.domain.model.journal.TravelJournalInfo
import com.weit.domain.model.journal.TravelJournalListInfo
import com.weit.domain.model.journal.TravelJournalVisibilityInfo
import com.weit.domain.repository.journal.TravelJournalRepository
import javax.inject.Inject

class UpdateTravelJournalVisibilityUseCase @Inject constructor(
    private val repository: TravelJournalRepository
) {
    suspend operator fun invoke(
        travelJournalVisibilityInfo: TravelJournalVisibilityInfo
    ): Result<Unit> =
        repository.updateTravelJournalVisibility(travelJournalVisibilityInfo)
}
