package com.weit.data.service

import com.weit.data.model.ListResponse
import com.weit.data.model.follow.FollowFollowingIdDTO
import com.weit.data.model.follow.FollowNumDTO
import com.weit.data.model.follow.FollowUserContentDTO
import com.weit.domain.model.follow.FollowFollowingIdInfo
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface FollowService {

    @POST("/api/v1/follows")
    suspend fun createFollow(
        @Body followFollowingIdInfo: FollowFollowingIdInfo,
    )

    @DELETE("/api/v1/follows")
    suspend fun deleteFollow(
        @Body followFollowingIdDTO: FollowFollowingIdDTO,
    )

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
}
