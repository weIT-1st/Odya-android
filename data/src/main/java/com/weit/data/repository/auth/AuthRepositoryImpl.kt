package com.weit.data.repository.auth

import android.content.Context
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.weit.data.BuildConfig
import com.weit.data.util.ContextProvider
import com.weit.domain.repository.auth.AuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val context: Context,
    private val contextProvider: ContextProvider,
) : AuthRepository {

    init {
        KakaoSdk.init(context, BuildConfig.KAKAO_LOGIN_KEY)
    }

    override suspend fun loginWithKakao(): Result<Unit> {
        return if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            loginWithKakaoTalk().first()
        } else {
            loginWithKakaoAccount().first()
        }
    }

    override suspend fun logout(): Result<Unit> {
        return logoutKakao().first()
    }

    private suspend fun loginWithKakaoTalk(): Flow<Result<Unit>> = callbackFlow {
        UserApiClient.instance.loginWithKakaoTalk(contextProvider.provide()) { _, error ->
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
}
