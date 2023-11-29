package com.weit.domain.repository.journal

import com.weit.domain.model.journal.TravelJournalInfo
import com.weit.domain.model.journal.TravelJournalListInfo

interface TravelJournalRepository {

    // 여행일지 생성 Api

    suspend fun getTravelJournal(
        travelJournalId: Long
    ): Result<TravelJournalInfo>

    suspend fun getTravelJournalList(
        size: Int?,
        lastTravelJournal: Long?
    ): Result<List<TravelJournalListInfo>>

    suspend fun getMyTravelJournalList(
        size: Int?,
        lastTravelJournal: Long?,
        placeId: String?,
    ): Result<List<TravelJournalListInfo>>

    suspend fun getFriendTravelJournalList(
        size: Int?,
        lastTravelJournal: Long?
    ): Result<List<TravelJournalListInfo>>

    suspend fun getRecommendTravelJournalList(
        size: Int?,
        lastTravelJournal: Long?
    ): Result<List<TravelJournalListInfo>>

    suspend fun getTaggedTravelJournalList(
        size: Int?,
        lastTravelJournal: Long?
    ): Result<List<TravelJournalListInfo>>


    // 여행 일지 수정 Api

    // 여행 일지 콘텐츠 수정 Api
    suspend fun deleteTravelJournal(travelJournalId: Long): Result<Unit>

    suspend fun deleteTravelJournalContent(travelJournalId: Long, travelJournalContentId: Long): Result<Unit>

    suspend fun deleteTravelJournalFriend(travelJournalId: Long): Result<Unit>
}
