package com.weit.data.service

import com.weit.data.model.ListResponse
import com.weit.data.model.follow.ExperiencedFriendDTO
import com.weit.data.model.follow.FollowFollowingId
import com.weit.data.model.follow.FollowNumDTO
import com.weit.data.model.follow.FollowUserContentDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface FollowService {

    @POST("/api/v1/follows")
    suspend fun createFollow(
        @Body followFollowingId: FollowFollowingId,
    )

    @HTTP(method = "DELETE", path = "/api/v1/follows", hasBody = true)
    suspend fun deleteFollow(
        @Body followFollowingId: FollowFollowingId,
    ): Response<Unit>

    @GET("/api/v1/follows/{userId}}/counts")
    suspend fun getFollowNumber(
        @Path("userId") userId: Long,
    ): FollowNumDTO

    @GET("/api/v1/follows/{userId}/followings")
    suspend fun getInfiniteFollowing(
        @Path("userId") userId: Long,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sortType") sortType: String,
    ): ListResponse<FollowUserContentDTO>

    @GET("/api/v1/follows/{userId}/followers")
    suspend fun getInfiniteFollower(
        @Path("userId") userId: Long,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sortType") sortType: String,
    ): ListResponse<FollowUserContentDTO>

    @GET("/api/v1/follows/{placeId}")
    suspend fun getExperiencedFriend(
        @Path("placeId") placeId: String,
    ): ExperiencedFriendDTO

    @GET("/api/v1/follows/may-know")
    suspend fun getMayknowUsers(
        @Query("size") size: Int?,
        @Query("lasId") lasId: Long?,
    ): ListResponse<FollowUserContentDTO>
}
