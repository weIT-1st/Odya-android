package com.weit.domain.usecase.auth

import com.weit.domain.repository.auth.AuthRepository
import javax.inject.Inject

class IsDuplicateEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(email: String): Result<Unit> =
        authRepository.isDuplicateEmail(email)
}
