package com.weit.data.source

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.user.UserApiClient
import com.weit.data.model.auth.KakaoAccessToken
import com.weit.data.model.auth.UserRegistration
import com.weit.data.model.auth.UserTokenDTO
import com.weit.data.model.auth.term.AgreedTermListDTO
import com.weit.data.model.auth.term.TermContentDTO
import com.weit.data.model.auth.term.TermDTO
import com.weit.data.model.auth.term.TermIdList
import com.weit.data.service.AuthService
import com.weit.domain.model.exception.auth.TokenNotFoundException
import com.weit.domain.model.exception.auth.UserNotFoundException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import retrofit2.Response
import javax.inject.Inject

class AuthDataSource @Inject constructor(
    private val auth: FirebaseAuth,
    private val service: AuthService,
) {

    suspend fun login(accessToken: KakaoAccessToken): UserTokenDTO = service.login(accessToken)

    suspend fun loginWithCustomToken(customToken: String): Result<FirebaseUser> = callbackFlow {
        auth.signInWithCustomToken(customToken).addOnSuccessListener {
            val user = it.user ?: throw UserNotFoundException()
            trySend(Result.success(user))
        }.addOnFailureListener {
            trySend(Result.failure<FirebaseUser>(it))
        }
        awaitClose { /* Do Nothing */ }
    }.first()

    suspend fun register(userRegistration: UserRegistration) {
        service.register(userRegistration)
    }

    fun hasKakaoToken(): Boolean =
        AuthApiClient.instance.hasToken()

    fun getKakaoToken(): Flow<String?> = callbackFlow {
        UserApiClient.instance.accessTokenInfo { _, _ ->
            trySend(getAccessToken())
        }
        awaitClose { /* Do Nothing */ }
    }

    private fun getAccessToken(): String =
        AuthApiClient.instance.tokenManagerProvider.manager.getToken()?.accessToken ?: throw TokenNotFoundException()

    fun getFirebaseToken(): String =
        auth.currentUser?.getIdToken(false)?.result?.token ?: throw TokenNotFoundException()

    suspend fun isDuplicateNickname(nickname: String): Response<Unit> =
        service.isDuplicateNickname(nickname)

    suspend fun isDuplicateEmail(email: String): Response<Unit> =
        service.isDuplicateEmail(email)

    suspend fun isDuplicatePhoneNum(phoneNum: String): Response<Unit> =
        service.isDuplicatePhonNumber(phoneNum)
    fun checkLogin(): Boolean {
        val user = auth.currentUser
        return user != null
    }

    suspend fun getTermList(): List<TermDTO> =
        service.getTermList()

    suspend fun getTermContent(termId: Long): TermContentDTO =
        service.getTermContent(termId)

    suspend fun getAgreedTerms(): AgreedTermListDTO =
        service.getAgreedTerms()

    suspend fun setTerms(termsIdList: TermIdList): Response<Unit> {
        return service.setTerms(termsIdList)
    }
}
