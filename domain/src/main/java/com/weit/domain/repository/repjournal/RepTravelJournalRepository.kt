package com.weit.domain.repository.repjournal

import com.weit.domain.model.journal.TravelJournalContentUpdateInfo
import com.weit.domain.model.journal.TravelJournalInfo
import com.weit.domain.model.journal.TravelJournalListInfo
import com.weit.domain.model.journal.TravelJournalRegistrationInfo
import com.weit.domain.model.journal.TravelJournalUpdateInfo
import com.weit.domain.model.journal.TravelJournalVisibilityInfo
import com.weit.domain.model.reptraveljournal.RepTravelJournalListInfo
import com.weit.domain.model.reptraveljournal.RepTravelJournalRequest

interface RepTravelJournalRepository {

    suspend fun registerRepTravelJournal(
        travelJournalId: Long,
    ): Result<Unit>

    suspend fun getMyRepTravelJournalList(
        repTravelJournalRequest : RepTravelJournalRequest
    ): Result<List<RepTravelJournalListInfo>>

    suspend fun getOtherRepTravelJournalList(
        repTravelJournalRequest : RepTravelJournalRequest,
        userId: Long
    ): Result<List<RepTravelJournalListInfo>>

    suspend fun deleteRepTravelJournal(
        repTravelJournalId: Long,
    ): Result<Unit>

}
