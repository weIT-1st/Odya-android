package com.weit.domain.usecase.auth

import com.weit.domain.model.auth.UserRegistrationInfo
import com.weit.domain.repository.auth.AuthRepository
import javax.inject.Inject

class RegisterUserUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(info: UserRegistrationInfo): Result<Unit> =
        repository.register(info)
}
