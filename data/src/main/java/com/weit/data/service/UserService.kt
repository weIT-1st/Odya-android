package com.weit.data.service

import com.weit.data.model.user.UserDTO
import com.weit.domain.model.user.User
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Path

interface UserService {

    @GET("/api/v1/users/me")
    suspend fun getUser(): UserDTO

    @PATCH("/api/v1/users/email")
    suspend fun updateEmail(
        @Body emailUpdateUser: User,
    )

    @PATCH("/api/v1/users/phone-number")
    suspend fun updatePhoneNumber(
        @Body phoneNumberUpdateUser: User,
    )

    @PATCH("/api/v1/users/information")
    suspend fun updateInformation(
        @Body informationUpdateUser: User,
    )

    @Multipart
    @PATCH("/api/v1/users/profile")
    suspend fun updateUserProfile(
        @Part profile: MultipartBody.Part?
    )
}
