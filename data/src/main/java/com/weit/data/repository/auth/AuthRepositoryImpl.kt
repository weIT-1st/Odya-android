package com.weit.data.repository.auth

import com.kakao.sdk.user.UserApiClient
import com.weit.data.model.auth.UserRegistration
import com.weit.data.source.AuthDataSource
import com.weit.domain.model.auth.UserRegistrationInfo
import com.weit.domain.repository.auth.AuthRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: AuthDataSource,
) : AuthRepository {

    override suspend fun logout(): Result<Unit> {
        return logoutKakao().first()
    }

    override suspend fun register(info: UserRegistrationInfo): Result<Unit> {
        return runCatching {
            authDataSource.register(info.toUserRegistration())
        }
    }

    private suspend fun logoutKakao(): Flow<Result<Unit>> = callbackFlow {
        UserApiClient.instance.logout { error ->
            if (error != null) {
                trySend(Result.failure(error))
            } else {
                trySend(Result.success(Unit))
            }
        }
        awaitClose { /* Do Nothing */ }
    }

    private fun UserRegistrationInfo.toUserRegistration(): UserRegistration =
        UserRegistration(
            name = name,
            email = email,
            phoneNumber = phoneNumber,
            nickname = nickname,
            gender = gender,
            birthday = birthday,
        )
}
