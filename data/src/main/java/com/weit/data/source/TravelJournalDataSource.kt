package com.weit.data.source

import com.weit.data.model.ListResponse
import com.weit.data.model.journal.TravelJournalDTO
import com.weit.data.model.journal.TravelJournalListDTO
import com.weit.data.service.TravelJournalService
import com.weit.domain.model.journal.TravelJournalListInfo
import retrofit2.Response
import java.util.concurrent.CopyOnWriteArrayList
import javax.inject.Inject

class TravelJournalDataSource @Inject constructor(
    private val service: TravelJournalService
) {

    // 여행일지 생성 API

    suspend fun getTravelJournal(travelJournalId: Long): TravelJournalDTO =
        service.getTravelJournal(travelJournalId)

    suspend fun getTravelJournalList(
        size: Int?,
        lastTravelJournal: Long?
    ): ListResponse<TravelJournalListDTO> =
        service.getTravelJournalList(size, lastTravelJournal)

    suspend fun getMyTravelJournalList(
        size: Int?,
        lastTravelJournal: Long?
    ): ListResponse<TravelJournalListDTO> =
        service.getMyTravelJournalList(size, lastTravelJournal)

    suspend fun getFriendTravelJournalList(
        size: Int?,
        lastTravelJournal: Long?
    ): ListResponse<TravelJournalListDTO> =
        service.getFriendTravelJournalList(size, lastTravelJournal)

    suspend fun getRecommendTravelJournalList(
        size: Int?,
        lastTravelJournal: Long?
    ): ListResponse<TravelJournalListDTO> =
        service.getRecommendTravelJournalList(size, lastTravelJournal)

    suspend fun getTaggedTravelJournalList(
        size: Int?,
        lastTravelJournal: Long?
    ): ListResponse<TravelJournalListDTO> =
        service.getTaggedTravelJournalList(size, lastTravelJournal)

    // 여행일지 수정 Api

    // 여행일지 콘텐츠 수정 Api

    suspend fun deleteTravelJournal(
        travelJournalId: Long
    ): Response<Unit> =
        service.deleteTravelJournal(travelJournalId)

    suspend fun deleteTravelJournalContent(
        travelJournalId: Long,
        travelJournalContentId: Long
    ): Response<Unit> =
        service.deleteTravelJournalContent(travelJournalId, travelJournalContentId)

    suspend fun deleteTravelJournalFriend(
        travelJournalId: Long
    ): Response<Unit> =
        service.deleteTravelJournalFriend(travelJournalId)
}
