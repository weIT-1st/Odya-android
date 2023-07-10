package com.weit.domain.repository.auth

interface LoginRepository {
    suspend fun loginWithKakao(): Result<Unit>
}
