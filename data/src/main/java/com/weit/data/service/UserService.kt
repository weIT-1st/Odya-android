package com.weit.data.service


import com.weit.data.model.user.UserDTO
import com.weit.domain.model.user.User
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH

interface UserService {

    @GET("/api/v1/users/me")
    suspend fun getUser(
    ) : UserDTO

    @PATCH("/api/v1/users/email")
    suspend fun updateEmail(
        @Body emailUpdateUser : User
    )

    @PATCH("/api/v1/users/phone-number")
    suspend fun updatePhoneNumber(
        @Body phoneNumberUpdateUser : User
    )

    @PATCH("/api/v1/users/information")
    suspend fun updateInformation(
        @Body informationUpdateUser : User
    )

}