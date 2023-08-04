package com.weit.domain.usecase.auth

import com.weit.domain.model.auth.IsDuplicateInfo
import com.weit.domain.repository.auth.AuthRepository
import javax.inject.Inject

class IsDuplicateEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend fun invoke(email: String): Result<IsDuplicateInfo?> =
        authRepository.isDuplicateEamil(email)
}