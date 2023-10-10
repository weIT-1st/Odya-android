package com.weit.data.service

import com.weit.data.model.ListResponse
import com.weit.data.model.community.CommunityCommentContentDTO
import com.weit.data.model.community.CommunityCommentRegistration
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CommunityCommentService {

    @POST("/api/v1/communities/{communityId}/comments")
    suspend fun registerCommunityComment(
        @Path("communityId") communityId: Long,
        @Body communityCommentRegistration: CommunityCommentRegistration,
    )

    @GET("/api/v1/communities/{communityId}/comments")
    suspend fun getCommunityComments(
        @Path("communityId") communityId: Long,
        @Query("size") size: Int?,
        @Query("lastId") lastCommunityId: Long?,
    ): ListResponse<CommunityCommentContentDTO>

    @PATCH("/api/v1/communities/{communityId}/comments/{commentId}")
    suspend fun updateCommunityComment(
        @Path("communityId") communityId: Long,
        @Path("commentId") commentId: Long,
        @Body communityCommentRegistration: CommunityCommentRegistration,
    ): Response<Unit>

    @DELETE("/api/v1/communities/{communityId}/comments/{commentId}")
    suspend fun deleteCommunityComment(
        @Path("communityId") communityId: Long,
        @Path("commentId") commentId: Long,
    ): Response<Unit>
}
