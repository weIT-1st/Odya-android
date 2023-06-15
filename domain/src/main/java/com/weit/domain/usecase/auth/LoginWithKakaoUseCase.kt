package com.weit.domain.usecase.auth

import com.weit.domain.repository.auth.AuthRepository
import javax.inject.Inject

class LoginWithKakaoUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(): Result<Unit> = repository.loginWithKakao()
}
