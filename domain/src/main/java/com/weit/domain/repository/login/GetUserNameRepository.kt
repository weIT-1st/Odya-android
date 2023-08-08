package com.weit.domain.repository.login

interface GetUserNameRepository {
    suspend fun setUsername(
        username: String
    ): Result<Unit>

    suspend fun getUsername(): Result<String?>
}