package com.weit.data.service

import com.weit.data.model.ListResponse
import com.weit.data.model.community.CommunityContentDTO
import com.weit.data.model.topic.FavoriteTopicDTO
import com.weit.data.model.topic.TopicDTO
import com.weit.data.model.topic.TopicRegistration
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CommunityService {

    @POST("/api/v1/communities/{communityId}/comments")
    suspend fun registerCommunityComment(
        @Path("communityId") communityId: Long,
        @Query("content") content: String,
    )

    @GET("/api/v1/communities/{communityId}/comments")
    suspend fun getCommunityComments(
        @Path("communityId") communityId: Long,
        @Query("size") size: Int?,
        @Query("lastId") lastCommunityId: Long?,
    ): ListResponse<CommunityContentDTO>

    @PATCH("/api/v1/communities/{communityId}/comments/{commentId}")
    suspend fun updateCommunityComment(
        @Path("communityId") communityId: Long,
        @Path("commentId") commentId: Long,
        @Query("content") content: String,
    ): Response<Unit>

    @GET("/api/v1/communities/{communityId}/comments/{commentId}")
    suspend fun deleteCommunityComment(
        @Path("communityId") communityId: Long,
        @Path("commentId") commentId: Long,
    ): Response<Unit>
}
