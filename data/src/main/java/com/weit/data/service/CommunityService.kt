package com.weit.data.service

import com.weit.data.model.ListResponse
import com.weit.data.model.community.CommunityContentDTO
import com.weit.data.model.community.CommunityMainContentDTO
import com.weit.data.model.community.CommunityMyActivityCommentContentDTO
import com.weit.data.model.community.CommunityMyActivityContentDTO
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface CommunityService {

    @Multipart
    @POST("/api/v1/communities")
    suspend fun registerCommunity(
        @Part community : MultipartBody.Part,
        @Part images: List<MultipartBody.Part>?
    )

    @Multipart
    @PUT("/api/v1/communities/{communityId}")
    suspend fun updateCommunity(
        @Path("communityId") communityId: Long,
        @Part community : MultipartBody.Part,
        @Part images: List<MultipartBody.Part>?
    ): Response<Unit>

    @GET("/api/v1/communities/{communityId}")
    suspend fun getDetailCommunity(
        @Path("communityId") communityId: Long,
    ): CommunityContentDTO

    @GET("/api/v1/communities")
    suspend fun getCommunities(
        @Query("size") size: Int?,
        @Query("lastId") lastCommunityId: Long?,
        @Query("sortType") sortType: String?,
        @Query("placeId") placeId: String?,
        ): ListResponse<CommunityMainContentDTO>

    @GET("/api/v1/communities/me")
    suspend fun getMyCommunities(
        @Query("size") size: Int?,
        @Query("lastId") lastCommunityId: Long?,
        @Query("sortType") sortType: String?,
    ): ListResponse<CommunityMyActivityContentDTO>

    @GET("/api/v1/communities/friends")
    suspend fun getFriendsCommunities(
        @Query("size") size: Int?,
        @Query("lastId") lastCommunityId: Long?,
        @Query("sortType") sortType: String?,
    ): ListResponse<CommunityMainContentDTO>

    @GET("/api/v1/communities/topic/{topicId}")
    suspend fun getCommunitiesByTopic(
        @Path("topicId") topicId: Long,
        @Query("size") size: Int?,
        @Query("lastId") lastCommunityId: Long?,
        @Query("sortType") sortType: String?,
    ): ListResponse<CommunityMainContentDTO>

    @DELETE("/api/v1/communities/{communityId}")
    suspend fun deleteCommunity(
        @Path("communityId") communityId: Long,
    ): Response<Unit>


    @GET("/api/v1/communities/like")
    suspend fun getMyLikeCommunities(
        @Query("size") size: Int?,
        @Query("lastId") lastCommunityId: Long?,
        @Query("sortType") sortType: String?,
    ): ListResponse<CommunityMyActivityContentDTO>

    @GET("/api/v1/communities/comment")
    suspend fun getMyCommentCommunities(
        @Query("size") size: Int?,
        @Query("lastId") lastCommunityId: Long?,
    ): ListResponse<CommunityMyActivityCommentContentDTO>

    @POST("/api/v1/communities/{communityId}/likes")
    suspend fun registerCommunityLike(
        @Path("communityId") communityId: Long,
    ): Response<Unit>

    @DELETE("/api/v1/communities/{communityId}/likes")
    suspend fun deleteCommunityLike(
        @Path("communityId") communityId: Long,
    ): Response<Unit>

}
