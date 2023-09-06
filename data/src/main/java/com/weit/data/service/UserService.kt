package com.weit.data.service

import androidx.room.Delete
import com.weit.data.model.user.UserByNicknameDTO
import com.weit.data.model.user.UserContentDTO
import com.weit.data.model.user.UserDTO
import com.weit.domain.model.user.User
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.Part
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

    @GET("/api/v1/users/search")
    suspend fun getUserByNickname(
        @Query("size") size: Int?,
        @Query("lastId") lastId: String?,
        @Query("nickname") nickname: String
    ): UserByNicknameDTO
}
