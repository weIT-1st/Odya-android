package com.weit.data.service

import com.weit.data.model.follow.FollowNumDTO
import com.weit.data.model.follow.FollowSearchDTO
import com.weit.domain.model.follow.FollowFollowingIdInfo
import retrofit2.http.*

interface FollowService {

    @POST("/api/v1/follows")
    @FormUrlEncoded
    suspend fun createFollow(
        @Body followFollowingIdInfo: FollowFollowingIdInfo
    )

    @DELETE("/api/v1/follows")
    @FormUrlEncoded
    suspend fun deleteFollow(
        @Body followFollowingIdInfo: FollowFollowingIdInfo
    )

    @GET("/api/v1/follows/1/counts")
    suspend fun getFollowNumber(
        @Path("userId") userId: Long,
    ): FollowNumDTO

    @GET("/api/v1/follows/1/followings?page=1&size=1&sortType=OLDEST")
    suspend fun getInfiniteFollowing(
        @Path("userId") userId: Long,
        @Query("page") page: Int?,
        @Query("size") size: Int?,
        @Query("sortType") sortType: String?
    ): FollowSearchDTO

    @GET("/api/v1/follows/1/followers?page=1&size=1&sortType=OLDEST")
    suspend fun getInfiniteFollower(
        @Path("userId") userId: Long,
        @Query("page") page: Int?,
        @Query("size") size: Int?,
        @Query("sortType") sortType: String?
    ): FollowSearchDTO
}