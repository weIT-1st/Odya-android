package com.weit.data.service

import com.weit.data.model.ListResponse
import com.weit.data.model.bookmark.JournalBookMarkDTO
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface BookMarkService {
    @POST("/api/v1/travel-journal-bookmarks/{travelJournalId}")
    suspend fun createJournalBookMark(
        @Path("travelJournalId") travelJournalId: Long
    ): Response<Unit>


    @GET("/api/v1/travel-journal-bookmarks/me")
    suspend fun getMyJournalBookMark(
        @Query("size") size: Int? = 10,
        @Query("lastId") lastId: Long?,
        @Query("sortType") sortType: String?
    ): ListResponse<JournalBookMarkDTO>

    @GET("/api/v1/travel-journal-bookmarks/{userId}")
    suspend fun getUserJournalBookMark(
        @Path("userId") userId: Long,
        @Query("size") size: Int? = 10,
        @Query("lastId") lastId: Long?,
        @Query("sortType") sortType: String?
    ): ListResponse<JournalBookMarkDTO>

    @DELETE("/api/v1/travel-journal-bookmarks/{travelJournalId}")
    suspend fun deleteJournalBookMark(
        @Path("travelJournalId") travelJournalId: Long
    ): Response<Unit>
}
