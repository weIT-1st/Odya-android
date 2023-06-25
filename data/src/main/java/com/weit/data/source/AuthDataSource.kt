package com.weit.data.source

import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.user.UserApiClient
import com.weit.data.model.auth.UserTokenDTO
import com.weit.data.service.AuthService
import com.weit.domain.model.auth.UserRegistrationInfo
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class AuthDataSource @Inject constructor(
    private val service: AuthService,
) {

    suspend fun login(accessToken: String): UserTokenDTO = service.login(accessToken)

    suspend fun register(info: UserRegistrationInfo) {
        service.register(
            name = info.name,
            email = info.email,
            phoneNumber = info.phoneNumber,
            nickname = info.nickname,
            gender = info.gender,
            birthday = info.birthday,
        )
    }

    fun hasKakaoToken(): Boolean =
        AuthApiClient.instance.hasToken()

    fun getKakaoToken(): Flow<String?> = callbackFlow {
        UserApiClient.instance.accessTokenInfo { _, _ ->
            trySend(getAccessToken())
        }
        awaitClose { /* Do Nothing */ }
    }

    private fun getAccessToken() =
        AuthApiClient.instance.tokenManagerProvider.manager.getToken()?.accessToken
}
