package com.weit.data.service

import com.weit.data.model.ListResponse
import com.weit.data.model.community.CommunityMyActivityContentDTO
import com.weit.data.model.image.UserImageResponseDTO
import com.weit.data.model.user.FcmToken
import com.weit.data.model.user.SearchUserContentDTO
import com.weit.data.model.user.UserDTO
import com.weit.data.model.user.UserStatisticsDTO
import com.weit.domain.model.user.User
import com.weit.domain.model.user.UserStatistics
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface UserService {

    @GET("/api/v1/users/me")
    suspend fun getUser(): UserDTO

    @PATCH("/api/v1/users/email")
    suspend fun updateEmail(
        @Body emailUpdateUser: User,
    ): Response<Unit>

    @PATCH("/api/v1/users/phone-number")
    suspend fun updatePhoneNumber(
        @Body phoneNumberUpdateUser: User,
    ): Response<Unit>

    @PATCH("/api/v1/users/information")
    suspend fun updateInformation(
        @Body informationUpdateUser: User,
    ): Response<Unit>

    @Multipart
    @PATCH("/api/v1/users/profile")
    suspend fun updateUserProfile(
        @Part profile: MultipartBody.Part?,
    ): Response<Unit>

    @PATCH("/api/v1/users/fcm-token")
    suspend fun updateFcmToken(
        @Body fcmToken: FcmToken,
    ): Response<Unit>

    @DELETE("/api/v1/users")
    suspend fun deleteUser(): Response<Unit>

    @GET("/api/v1/users/search")
    suspend fun getSearchUsers(
        @Query("size") size: Int?,
        @Query("lastId") userId: Long?,
        @Query("nickname") nickname: String,
    ): ListResponse<SearchUserContentDTO>

    @GET("/api/v1/users/{userId}/life-shots")
    suspend fun getUserLifeShot(
        @Path("userId") userId: Long,
        @Query("size") size: Int?,
        @Query("lastId") imageId: Long?,
    ): ListResponse<UserImageResponseDTO>

    @GET("/api/v1/users/{userId}/statistics")
    suspend fun getUserStatistics(
        @Path("userId") userId: Long,
    ): UserStatisticsDTO
}
