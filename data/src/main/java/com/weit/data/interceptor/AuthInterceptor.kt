package com.weit.data.interceptor

import com.weit.data.source.AuthDataSource
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val dataSource: AuthDataSource,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        // TODO 매번 토큰을 요청하는건 비효율적으로 보임
        val authorization = BEARER_PREFIX + dataSource.getFirebaseToken()
        val newRequest = chain.request().newBuilder().apply {
            addHeader(AUTHORIZATION_HEADER, authorization)
        }
        return chain.proceed(newRequest.build())
    }

    companion object {
        private const val AUTHORIZATION_HEADER = "Authorization"
        private const val BEARER_PREFIX = "Bearer "
    }
}
