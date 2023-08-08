package com.weit.domain.repository.auth

import com.weit.domain.model.auth.UserRegistrationInfo

interface AuthRepository {
    suspend fun logout(): Result<Unit>

    suspend fun register(
        info: UserRegistrationInfo,
    ): Result<Unit>

    suspend fun isDuplicateNickname(nickname: String): Result<Unit>
    suspend fun isDuplicateEmail(email: String): Result<Unit>
    suspend fun isDuplicatePhoneNum(phoneNum: String): Result<Unit>

}
