package com.weit.domain.repository.auth

interface AuthRepository {
    suspend fun logout(): Result<Unit>
}
