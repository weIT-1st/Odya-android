package com.weit.data.service

import com.weit.data.model.ListResponse
import com.weit.data.model.community.CommentContentDTO
import com.weit.data.model.community.CommentRegistration
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CommentService {

    @POST("/api/v1/communities/{communityId}/comments")
    suspend fun registerCommunityComment(
        @Path("communityId") communityId: Long,
        @Body commentRegistration: CommentRegistration,
    )

    @GET("/api/v1/communities/{communityId}/comments")
    suspend fun getCommunityComments(
        @Path("communityId") communityId: Long,
        @Query("size") size: Int?,
        @Query("lastId") lastCommunityId: Long?,
    ): ListResponse<CommentContentDTO>

    @PATCH("/api/v1/communities/{communityId}/comments/{commentId}")
    suspend fun updateCommunityComment(
        @Path("communityId") communityId: Long,
        @Path("commentId") commentId: Long,
        @Body commentRegistration: CommentRegistration,
    ): Response<Unit>

    @DELETE("/api/v1/communities/{communityId}/comments/{commentId}")
    suspend fun deleteCommunityComment(
        @Path("communityId") communityId: Long,
        @Path("commentId") commentId: Long,
    ): Response<Unit>
}
