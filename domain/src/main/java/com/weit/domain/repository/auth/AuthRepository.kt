package com.weit.domain.repository.auth

import com.weit.domain.model.auth.UserRegistrationInfo

interface AuthRepository {
    suspend fun logout(): Result<Unit>

    suspend fun register(
        info: UserRegistrationInfo,
    ): Result<Unit>
}
