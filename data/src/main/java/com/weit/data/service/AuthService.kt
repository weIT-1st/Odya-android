package com.weit.data.service

import com.weit.data.model.auth.KakaoAccessToken
import com.weit.data.model.auth.UserRegistration
import com.weit.data.model.auth.UserTokenDTO
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {

    @POST("/api/v1/auth/login/kakao")
    suspend fun login(
        @Body accessToken: KakaoAccessToken,
    ): UserTokenDTO

    @POST("/api/v1/auth/register/kakao")
    suspend fun register(
        @Body userRegistration: UserRegistration,
    )
}
