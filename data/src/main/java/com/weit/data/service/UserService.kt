package com.weit.data.service


import android.provider.ContactsContract.CommonDataKinds.Nickname
import com.weit.data.model.user.UserDTO
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.PATCH

interface UserService {

    @GET("/api/v1/users/me")
    suspend fun getUser(
    ) : UserDTO

    @PATCH("/api/v1/users/email")
    suspend fun updateEmail(
        @Field("email") email: String
    )

    @PATCH("/api/v1/users/phone-number")
    suspend fun updatePhoneNumber(
        @Field("phoneNumber") phoneNumber : String
    )

    @PATCH("/api/v1/users/information")
    suspend fun updateInformation(
        @Field("nickname") nickname: String
    )

}