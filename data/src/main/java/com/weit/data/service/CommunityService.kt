package com.weit.data.service

import com.weit.data.model.ListResponse
import com.weit.data.model.community.CommunityContentDTO
import com.weit.data.model.community.CommunityMainContentDTO
import com.weit.domain.model.community.CommunityContent
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap
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
    @PATCH("/api/v1/communities/{communityId}")
    suspend fun updateCommunity(
        @Path("communityId") communityId: Long,
        @Part community : MultipartBody.Part,
        @Part images: List<MultipartBody.Part>?
    )

    @GET("/api/v1/communities/{communityId}")
    suspend fun getDetailCommunity(
        @Path("communityId") communityId: Long,
    ): CommunityContentDTO

    @GET("/api/v1/communities")
    suspend fun getCommunities(
        @Query("size") size: Int?,
        @Query("lastId") lastCommunityId: Long?,
        @Query("sortType") sortType: String?,
        ): ListResponse<CommunityMainContentDTO>

    @GET("/api/v1/communities/me")
    suspend fun getMyCommunities(
        @Query("size") size: Int?,
        @Query("lastId") lastCommunityId: Long?,
        @Query("sortType") sortType: String?,
    ): ListResponse<CommunityMainContentDTO>

    @GET("/api/v1/communities/friends")
    suspend fun getFriendsCommunities(
        @Query("size") size: Int?,
        @Query("lastId") lastCommunityId: Long?,
        @Query("sortType") sortType: String?,
    ): ListResponse<CommunityMainContentDTO>

    @DELETE("/api/v1/communities/{communityId}")
    suspend fun deleteCommunity(
        @Path("communityId") communityId: Long,
    ): Response<Unit>


}
