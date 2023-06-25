package com.weit.data.service

import com.weit.data.model.auth.UserTokenDTO
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AuthService {

    @POST("/api/v1/auth/login")
    @FormUrlEncoded
    suspend fun login(
        @Field("accessToken") accessToken: String,
    ): UserTokenDTO

    @POST("/api/v1/auth/register/kakao")
    @FormUrlEncoded
    suspend fun register(
        @Field("username") name: String,
        @Field("email") email: String?,
        @Field("phoneNumber") phoneNumber: String?,
        @Field("nickname") nickname: String,
        @Field("gender") gender: String,
        @Field("birthday") birthday: List<Int>,
    )
}
