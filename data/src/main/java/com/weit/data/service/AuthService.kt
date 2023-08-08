package com.weit.data.service

import com.weit.data.model.auth.KakaoAccessToken
import com.weit.data.model.auth.UserRegistration
import com.weit.data.model.auth.UserTokenDTO
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthService {

    @POST("/api/v1/auth/login/kakao")
    suspend fun login(
        @Body accessToken: KakaoAccessToken,
    ): UserTokenDTO

    @POST("/api/v1/auth/register/kakao")
    suspend fun register(
        @Body userRegistration: UserRegistration,
    )

    @GET("/api/v1/auth/validate/nickname")
    suspend fun isDuplicateNickname(
        @Query("value") nickname: String
    ): Result<Unit>

    @GET("/api/v1/auth/validate/email")
    suspend fun isDuplicateEmail(
        @Query("value") email: String
    ): Result<Unit>

    @GET("/api/v1/auth/validate/phone-number")
    suspend fun isDuplicatePhonNumber(
        @Query("value") phoneNum: String
    ): Result<Unit>
}
