package com.weit.domain.repository.login

interface UserInfoRepository {
    suspend fun setUsername(
        username: String
    ): Result<Unit>

    suspend fun getUsername(): Result<String?>

    suspend fun setNickname(
        nickname: String
    ): Result<Unit>

    suspend fun getNickname(): Result<String?>
}