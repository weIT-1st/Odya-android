package com.weit.data.source

import com.weit.data.model.ListResponse
import com.weit.data.model.journal.TravelJournalDTO
import com.weit.data.model.journal.TravelJournalListDTO
import com.weit.data.model.journal.TravelJournalVisibility
import com.weit.data.service.TravelJournalService
import okhttp3.MultipartBody
import retrofit2.Response
import javax.inject.Inject

class TravelJournalDataSource @Inject constructor(
    private val service: TravelJournalService,
) {
    suspend fun registerTravelJournal(
        travelJournal: MultipartBody.Part,
        images: List<MultipartBody.Part>
        ) {
        service.registerJournal(travelJournal, images)
    }

    suspend fun getTravelJournal(travelJournalId: Long): TravelJournalDTO =
        service.getTravelJournal(travelJournalId)

    suspend fun getTravelJournalList(
        size: Int?,
        lastTravelJournal: Long?
    ): ListResponse<TravelJournalListDTO> =
        service.getTravelJournalList(size, lastTravelJournal)

    suspend fun getMyTravelJournalList(
        size: Int?,
        lastTravelJournal: Long?,
        placeId: String?
    ): ListResponse<TravelJournalListDTO> =
        service.getMyTravelJournalList(size, lastTravelJournal, placeId)

    suspend fun getFriendTravelJournalList(
        size: Int?,
        lastTravelJournal: Long?,
        placeId: String?
    ): ListResponse<TravelJournalListDTO> =
        service.getFriendTravelJournalList(size, lastTravelJournal, placeId)

    suspend fun getRecommendTravelJournalList(
        size: Int?,
        lastTravelJournal: Long?,
        placeId: String?
    ): ListResponse<TravelJournalListDTO> =
        service.getRecommendTravelJournalList(size, lastTravelJournal, placeId)

    suspend fun getTaggedTravelJournalList(
        size: Int?,
        lastTravelJournal: Long?
    ): ListResponse<TravelJournalListDTO> =
        service.getTaggedTravelJournalList(size, lastTravelJournal)

    suspend fun updateTravelJournal(
        travelJournalId: Long,
        travelJournalUpdate: MultipartBody.Part,
    ): Response<Unit> =
        service.updateTravelJournal(travelJournalId, travelJournalUpdate)

    suspend fun updateTravelJournalContent(
        travelJournalId: Long,
        travelJournalContentId: Long,
        travelJournalContentUpdate: MultipartBody.Part,
        images: List<MultipartBody.Part>
    ): Response<Unit> =
        service.updateTravelJournalContent(travelJournalId, travelJournalContentId, travelJournalContentUpdate, images)

    suspend fun addTravelJournalContent(
        travelJournalId: Long,
        travelJournalContent: MultipartBody.Part
    ){
        service.addTravelJournalContent(travelJournalId, travelJournalContent)
    }

    suspend fun updateTravelJournalVisibility(
        travelJournalId: Long,
        travelJournalVisibility: TravelJournalVisibility
    ): Response<Unit> =
        service.updateTravelJournalVisibility(travelJournalId,travelJournalVisibility)

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
