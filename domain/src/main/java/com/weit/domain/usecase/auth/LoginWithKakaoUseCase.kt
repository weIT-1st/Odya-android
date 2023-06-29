package com.weit.domain.usecase.auth

import com.weit.domain.model.auth.UserToken
import com.weit.domain.repository.auth.LoginRepository
import javax.inject.Inject

/**
 * 카카오 로그인은 반드시 Activity, Fragment에서 주입
 */
class LoginWithKakaoUseCase @Inject constructor(
    private val repository: LoginRepository,
) {
    suspend operator fun invoke(): Result<UserToken> = repository.loginWithKakao()
}
