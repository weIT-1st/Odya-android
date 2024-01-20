package com.weit.data.source

import com.weit.data.model.ListResponse
import com.weit.data.model.journal.TravelJournalDTO
import com.weit.data.model.journal.TravelJournalListDTO
import com.weit.data.model.journal.TravelJournalVisibility
import com.weit.data.model.reptraveljournal.RepTravelJournalListDTO
import com.weit.data.service.RepTravelJournalService
import com.weit.data.service.TravelJournalService
import com.weit.domain.model.journal.TravelJournalListInfo
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import java.util.concurrent.CopyOnWriteArrayList
import javax.inject.Inject

class RepTravelJournalDataSource @Inject constructor(
    private val service: RepTravelJournalService
) {

    suspend fun registerRepTravelJournal(
        travelJournalId: Long,
        ) {
        service.registerRepJournal(travelJournalId)
    }

    suspend fun getMyRepTravelJournalList(
        size: Int?,
        lastRepTravelJournalId: Long?,
        sortType: String?
    ): ListResponse<RepTravelJournalListDTO> =
        service.getMyRepTravelJournal(size, lastRepTravelJournalId, sortType)

    suspend fun getOtherRepTravelJournalList(
        userId: Long,
        size: Int?,
        lastRepTravelJournalId: Long?,
        sortType: String?
    ): ListResponse<RepTravelJournalListDTO> =
        service.getOtherTravelJournalList(userId, size, lastRepTravelJournalId, sortType)

    suspend fun deleteRepTravelJournal(repTravelJournalId: Long): Response<Unit> {
        return service.deleteRepTravelJournal(repTravelJournalId)
    }
}
