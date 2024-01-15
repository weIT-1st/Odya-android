package com.weit.data.service

import com.weit.data.model.ListResponse
import com.weit.data.model.favoritePlace.FavoritePlaceRegistration
import com.weit.data.model.journal.TravelJournalDTO
import com.weit.data.model.journal.TravelJournalListDTO
import com.weit.data.model.journal.TravelJournalVisibility
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

interface TravelJournalService {

    @Multipart
    @POST("/api/v1/travel-journals")
    suspend fun registerJournal(
        @Part travelJournal: MultipartBody.Part,
        @Part images: List<MultipartBody.Part>
    )


    @GET("/api/v1/travel-journals/")
    suspend fun getTravelJournal(
        @Path("travelJournalId") travelJournalId: Long
    ): TravelJournalDTO

    @GET("/api/v1/travel-journals")
    suspend fun getTravelJournalList(
        @Query("size") size: Int? = 10,
        @Query("lastId") lastTravelJournalId: Long?
    ): ListResponse<TravelJournalListDTO>

    @GET("/api/v1/travel-journals/me")
    suspend fun getMyTravelJournalList(
        @Query("size") size: Int? = 10,
        @Query("lastId") lastTravelJournalId: Long?,
        @Query("placeId") placeId: String?
    ): ListResponse<TravelJournalListDTO>

    @GET("/api/v1/travel-journals/friends")
    suspend fun getFriendTravelJournalList(
        @Query("size") size: Int? = 10,
        @Query("lastId") lastTravelJournalId: Long?,
        @Query("placeId") placeId: String?,
    ): ListResponse<TravelJournalListDTO>

    @GET("/api/v1/travel-journals/recommends")
    suspend fun getRecommendTravelJournalList(
        @Query("size") size: Int? = 10,
        @Query("lastId") lastTravelJournalId: Long?,
        @Query("placeId") placeId: String?,
    ): ListResponse<TravelJournalListDTO>

    @GET("/api/v1/travel-journals/tagged")
    suspend fun getTaggedTravelJournalList(
        @Query("size") size: Int? = 10,
        @Query("lastId") lastTravelJournalId: Long?
    ): ListResponse<TravelJournalListDTO>

    @Multipart
    @PUT("/api/v1/travel-journals/{travelJournalId}")
    suspend fun updateTravelJournal(
        @Path("travelJournalId") travelJournalId: Long,
        @Part travelJournalUpdate: MultipartBody.Part,
    )

    @Multipart
    @PUT("/api/v1/travel-journals/{travelJournalId}/{travelJournalContentId}")
    suspend fun updateTravelJournalContent(
        @Path("travelJournalId") travelJournalId: Long,
        @Path("travelJournalContentId") travelJournalContentId: Long,
        @Part travelJournalContentUpdate: MultipartBody.Part,
        @Part images: List<MultipartBody.Part>
    )

    @PATCH("/api/v1/travel-journals/{travelJournalId}/visibility")
    suspend fun updateTravelJournalVisibility(
        @Path("travelJournalId") travelJournalId: Long,
        @Body visibility: TravelJournalVisibility,
    ): Response<Unit>

    @DELETE("/api/v1/travel-journals/{travelJournalId}")
    suspend fun deleteTravelJournal(
        @Path("travelJournalId") travelJournalId: Long
    ): Response<Unit>

    @DELETE("/api/v1/travel-journals/{travelJournalId}}/{travelJournalContentId}")
    suspend fun deleteTravelJournalContent(
        @Path("travelJournalId") travelJournalId: Long,
        @Path("travelJournalContentId") travelJournalContentId: Long
    ): Response<Unit>

    @DELETE("/api/v1/travel-journals/{travelJournalId}/travelCompanion")
    suspend fun deleteTravelJournalFriend(
        @Path("travelJournalId") travelJournalId: Long,
    ) : Response<Unit>
}
