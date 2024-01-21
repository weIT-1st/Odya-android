package com.weit.data.service

import com.weit.data.model.ListResponse
import com.weit.data.model.favoritePlace.FavoritePlaceRegistration
import com.weit.data.model.journal.TravelJournalDTO
import com.weit.data.model.journal.TravelJournalListDTO
import com.weit.data.model.journal.TravelJournalVisibility
import com.weit.data.model.reptraveljournal.RepTravelJournalListDTO
import com.weit.domain.model.reptraveljournal.RepTravelJournalListInfo
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface RepTravelJournalService {

    @POST("/api/v1/rep-travel-journals/{travelJournalId}")
    suspend fun registerRepJournal(
        @Path("travelJournalId") travelJournalId: Long,
    )

    @GET("/api/v1/rep-travel-journals/me")
    suspend fun getMyRepTravelJournal(
        @Query("size") size: Int? = 10,
        @Query("lastId") lastRepTravelJournalId: Long?,
        @Query("sortType") sortType: String?
    ): ListResponse<RepTravelJournalListDTO>

    @GET("/api/v1/rep-travel-journals/{userId}")
    suspend fun getOtherTravelJournalList(
        @Path("userId") userId: Long,
        @Query("size") size: Int? = 10,
        @Query("lastId") lastRepTravelJournalId: Long?,
        @Query("sortType") sortType: String?
    ): ListResponse<RepTravelJournalListDTO>

    @DELETE("/api/v1/rep-travel-journals/{repTravelJournalId}")
    suspend fun deleteRepTravelJournal(
        @Path("repTravelJournalId") repTravelJournalId: Long,
    ): Response<Unit>
}
