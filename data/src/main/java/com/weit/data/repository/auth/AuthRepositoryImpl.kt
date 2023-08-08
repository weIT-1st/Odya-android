package com.weit.data.repository.auth

import com.kakao.sdk.user.UserApiClient
import com.weit.data.model.auth.UserRegistration
import com.weit.data.source.AuthDataSource
import com.weit.domain.model.auth.UserRegistrationInfo
import com.weit.domain.model.exception.UnKnownException
import com.weit.domain.model.exception.auth.DuplicatedSomethingException
import com.weit.domain.model.exception.auth.InvalidSomethingException
import com.weit.domain.repository.auth.AuthRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import okhttp3.internal.http.HTTP_BAD_REQUEST
import okhttp3.internal.http.HTTP_CONFLICT
import retrofit2.HttpException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: AuthDataSource,
) : AuthRepository {

    override suspend fun logout(): Result<Unit> {
        return logoutKakao().first()
    }

    override suspend fun register(info: UserRegistrationInfo): Result<Unit> {
        val result = runCatching {
            authDataSource.register(info.toUserRegistration())
        }
        return if (result.isSuccess) {
            Result.success(Unit)
        } else {
            Result.failure(handleRegistrationError(result.exceptionOrNull()!!))
        }
    }

    private fun handleRegistrationError(t: Throwable): Throwable {
        // TODO 에러 코드가 추가 되면 에러 처리 세분화
        return if (t is HttpException) {
            when (t.code()) {
                HTTP_CONFLICT -> DuplicatedSomethingException()
                HTTP_BAD_REQUEST -> InvalidSomethingException()
                else -> UnKnownException()
            }
        } else {
            t
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
            birthday = listOf(birthday.year, birthday.monthValue, birthday.dayOfMonth),
        )

    override fun verifyCurrentUser(): Boolean =
        authDataSource.checkLogin()
}
