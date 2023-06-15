package com.weit.domain.repository.auth

interface AuthRepository {
    suspend fun loginWithKakao(): Result<Unit>
    suspend fun logout(): Result<Unit>
}
