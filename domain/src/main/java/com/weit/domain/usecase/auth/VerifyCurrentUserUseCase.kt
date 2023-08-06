package com.weit.domain.usecase.auth

import com.weit.domain.repository.auth.AuthRepository
import javax.inject.Inject

class VerifyCurrentUserUseCase @Inject constructor(
    private val loginRepository: AuthRepository,
) {
    operator fun invoke(): Boolean =
        loginRepository.verifyCurrentUser()
}
