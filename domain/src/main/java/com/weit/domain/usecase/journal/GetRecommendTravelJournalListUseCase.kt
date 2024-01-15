package com.weit.domain.usecase.journal

import com.weit.domain.model.journal.TravelJournalListInfo
import com.weit.domain.repository.journal.TravelJournalRepository
import javax.inject.Inject

class GetRecommendTravelJournalListUseCase @Inject constructor(
    private val repository: TravelJournalRepository
) {
    suspend operator fun invoke(
        size: Int?,
        lastTravelJournal: Long?,
        placeId: String?
    ): Result<List<TravelJournalListInfo>> =
        repository.getRecommendTravelJournalList(size, lastTravelJournal, placeId)
}
