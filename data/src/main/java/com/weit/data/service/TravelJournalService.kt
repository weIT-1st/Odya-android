package com.weit.data.service

import com.weit.data.model.ListResponse
import com.weit.data.model.journal.TravelJournalDTO
import com.weit.data.model.journal.TravelJournalListDTO
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TravelJournalService {

    // 여행일지 생성 API

    @GET("/api/v1/travel-journals")
    suspend fun getTravelJournal(
        @Path("travelJournalId") travelJournalId: Long
    ): TravelJournalDTO

    @GET("/api/v1/travel-journals/{size}/{lastId}")
    suspend fun getTravelJournalList(
        @Query("size") size: Int? = 10,
        @Query("lastId") lastTravelJournalId: Long?
    ): ListResponse<TravelJournalListDTO>

    @GET("/api/v1/travel-journals/me/{size}/{lastId}")
    suspend fun getMyTravelJournalList(
        @Query("size") size: Int? = 10,
        @Query("lastId") lastTravelJournalId: Long?
    ): ListResponse<TravelJournalListDTO>

    @GET("/api/v1/travel-journals/friends /{size}/{lastId}")
    suspend fun getFriendTravelJournalList(
        @Query("size") size: Int? = 10,
        @Query("lastId") lastTravelJournalId: Long?
    ): ListResponse<TravelJournalListDTO>

    @GET("/api/v1/travel-journals/recommends/{size}/{lastId}")
    suspend fun getRecommendTravelJournalList(
        @Query("size") size: Int? = 10,
        @Query("lastId") lastTravelJournalId: Long?
    ): ListResponse<TravelJournalListDTO>

    @GET("/api/v1/travel-journals/tagged/{size}/{lastId}")
    suspend fun getTaggedTravelJournalList(
        @Query("size") size: Int? = 10,
        @Query("lastId") lastTravelJournalId: Long?
    ): ListResponse<TravelJournalListDTO>

    // 여행 일지 수정 Api

    // 여행일지 콘텐츠 수정 Api

    @DELETE("/api/v1/travel-journals/")
    suspend fun deleteTravelJournal(
        @Path("travelJournalId") travelJournalId: Long
    ): Response<Unit>

    @DELETE("")
    suspend fun deleteTravelJournalContent(
        @Path("travelJournalId") travelJournalId: Long,
        @Path("travelJournalContentId") travelJournalContentId: Long
    ): Response<Unit>

    @DELETE("/api/v1/travel-journals/1/travelCompanion")
    suspend fun deleteTravelJournalFriend(
        @Path("travelJournalId") travelJournalId: Long,
    ) : Response<Unit>
}
