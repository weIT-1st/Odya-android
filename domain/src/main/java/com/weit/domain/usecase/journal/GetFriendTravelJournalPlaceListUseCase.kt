package com.weit.domain.usecase.journal

import com.weit.domain.model.journal.TravelJournalInfo
import com.weit.domain.model.journal.TravelJournalListInfo
import com.weit.domain.model.journal.TravelJournalPlaceList
import com.weit.domain.repository.journal.TravelJournalRepository
import javax.inject.Inject

class GetFriendTravelJournalPlaceListUseCase @Inject constructor(
    private val repository: TravelJournalRepository
) {
    suspend operator fun invoke(
        size: Int?,
        lastTravelJournal: Long?
    ): List<TravelJournalPlaceList>
    {
        val myJournalListResult = repository.getFriendTravelJournalList(size, lastTravelJournal)
        val travelJournal = emptyList<TravelJournalPlaceList>().toMutableList()

        if (myJournalListResult.isSuccess){
            val journalList = myJournalListResult.getOrThrow()
            journalList.forEach {

                val journalInfoResult = repository.getTravelJournal(it.travelJournalId)
                if (journalInfoResult.isSuccess){
                    val journalInfo = journalInfoResult.getOrThrow()
                    travelJournal.add(TravelJournalPlaceList(
                        it.travelJournalId,
                        journalInfo.travelJournalContents[0].placeId,
                        journalInfo.travelJournalContents[0].latitude[0],
                        journalInfo.travelJournalContents[0].longitude[0],
                        journalInfo.travelJournalContents[0].travelJournalContentImages[0]
                    ))
                }
            }
        }

        return travelJournal
    }
}
