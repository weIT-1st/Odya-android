package com.weit.domain.usecase.auth

import com.weit.domain.repository.auth.AuthRepository
import javax.inject.Inject

class verifyCurrentUserUseCase @Inject constructor(
    private val loginRepository: AuthRepository,
) {
    suspend operator fun invoke(): Boolean =
        loginRepository.verifyCurrentUser()
}
