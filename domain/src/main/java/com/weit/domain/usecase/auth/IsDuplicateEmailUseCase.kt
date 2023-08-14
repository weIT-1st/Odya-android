package com.weit.domain.usecase.auth

import com.weit.domain.repository.auth.AuthRepository
import javax.inject.Inject

class IsDuplicateEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(email: String): Boolean {
        var IsDuplicate = false

        authRepository.isDuplicateEmail(email).onSuccess {
            IsDuplicate = true
        }.onFailure {
            IsDuplicate = false
        }
        return IsDuplicate
    }
}
