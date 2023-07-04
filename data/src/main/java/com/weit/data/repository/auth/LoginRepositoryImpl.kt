package com.weit.data.repository.auth

import android.content.Context
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.orhanobut.logger.Logger
import com.weit.data.model.auth.KakaoAccessToken
import com.weit.data.model.auth.UserTokenDTO
import com.weit.data.source.AuthDataSource
import com.weit.domain.model.auth.UserToken
import com.weit.domain.model.exception.UnKnownException
import com.weit.domain.model.exception.auth.NeedUserRegistrationException
import com.weit.domain.model.exception.auth.ServerLoginFailedException
import com.weit.domain.model.exception.auth.UserNotFoundException
import com.weit.domain.repository.auth.LoginRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.internal.http.HTTP_INTERNAL_SERVER_ERROR
import okhttp3.internal.http.HTTP_UNAUTHORIZED
import org.json.JSONObject
import retrofit2.HttpException
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val context: Context,
    private val authDataSource: AuthDataSource,
) : LoginRepository {
    override suspend fun loginWithKakao(): Result<UserToken> {
        // TODO 유효 토큰 검사 후 자동 로그인
        val result = if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            loginWithKakaoTalk().first()
        } else {
            loginWithKakaoAccount().first()
        }
        return if (result.isSuccess) {
            loginToServer()
        } else {
            Logger.t("MainTest").i("${result.javaClass.name} ${result.exceptionOrNull()?.message}")
            Result.failure(result.exceptionOrNull() ?: Exception())
        }
    }

    private suspend fun loginWithKakaoTalk(): Flow<Result<Unit>> = callbackFlow {
        UserApiClient.instance.loginWithKakaoTalk(context) { _, error ->
            if (error != null) {
                if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                    trySend(Result.failure(error))
                }
                CoroutineScope(Dispatchers.IO).launch {
                    trySend(loginWithKakaoAccount().first())
                }
            } else {
                trySend(Result.success(Unit))
            }
        }
        awaitClose { /* Do Nothing */ }
    }

    private suspend fun loginWithKakaoAccount(): Flow<Result<Unit>> = callbackFlow {
        UserApiClient.instance.loginWithKakaoAccount(context) { _, error ->
            if (error != null) {
                trySend(Result.failure(error))
            } else {
                trySend(Result.success(Unit))
            }
        }
        awaitClose { /* Do Nothing */ }
    }

    private suspend fun loginToServer(): Result<UserToken> {
        return authDataSource.getKakaoToken().first()?.let { token ->
            val result = kotlin.runCatching {
                authDataSource.login(KakaoAccessToken(token)).toUserToken()
            }
            if (result.isSuccess) {
                Result.success(result.getOrThrow())
            } else {
                Result.failure(handleServerLoginError(result.exceptionOrNull()!!))
            }
        } ?: Result.failure(UserNotFoundException())
    }

    private fun handleServerLoginError(t: Throwable): Throwable {
        return if (t is HttpException) {
            when (t.code()) {
                HTTP_UNAUTHORIZED -> {
                    val message = t.response()?.errorBody()?.string().toString()
                    NeedUserRegistrationException(getUsername(message))
                }
                HTTP_INTERNAL_SERVER_ERROR -> ServerLoginFailedException()
                else -> UnKnownException()
            }
        } else {
            t
        }
    }

    private fun getUsername(message: String): String = JSONObject(message)["username"].toString()

    private fun UserTokenDTO.toUserToken() = UserToken(token)
}
