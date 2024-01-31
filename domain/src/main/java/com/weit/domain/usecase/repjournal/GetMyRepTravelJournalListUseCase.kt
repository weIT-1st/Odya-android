package com.weit.domain.usecase.repjournal

import com.weit.domain.model.journal.TravelJournalInfo
import com.weit.domain.model.journal.TravelJournalListInfo
import com.weit.domain.model.reptraveljournal.RepTravelJournalListInfo
import com.weit.domain.model.reptraveljournal.RepTravelJournalRequest
import com.weit.domain.repository.journal.TravelJournalRepository
import com.weit.domain.repository.repjournal.RepTravelJournalRepository
import javax.inject.Inject

class GetMyRepTravelJournalListUseCase @Inject constructor(
    private val repository: RepTravelJournalRepository
) {
    suspend operator fun invoke(
        repTravelJournalRequest : RepTravelJournalRequest
    ): Result<List<RepTravelJournalListInfo>> =
        repository.getMyRepTravelJournalList(repTravelJournalRequest)
}
