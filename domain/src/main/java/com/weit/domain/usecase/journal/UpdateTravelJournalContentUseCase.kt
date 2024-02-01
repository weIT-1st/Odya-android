package com.weit.domain.usecase.journal

import com.weit.domain.model.journal.TravelJournalContentUpdateInfo
import com.weit.domain.repository.journal.TravelJournalRepository
import javax.inject.Inject

class UpdateTravelJournalContentUseCase @Inject constructor(
    private val repository: TravelJournalRepository
) {
    suspend operator fun invoke(
        travelJournalId: Long,
        travelJournalContentId: Long,
        travelJournalContentUpdateInfo: TravelJournalContentUpdateInfo,
        travelJournalContentImages: List<String>
    ): Result<Unit> =
        repository.updateTravelJournalContent(travelJournalId, travelJournalContentId, travelJournalContentUpdateInfo, travelJournalContentImages)
}
