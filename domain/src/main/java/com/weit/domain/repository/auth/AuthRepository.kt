package com.weit.domain.repository.auth

import com.weit.domain.model.auth.IsDuplicateInfo
import com.weit.domain.model.auth.UserRegistrationInfo

interface AuthRepository {
    suspend fun logout(): Result<Unit>

    suspend fun register(
        info: UserRegistrationInfo,
    ): Result<Unit>

    suspend fun isDuplicateNickname(nickname: String): Result<IsDuplicateInfo?>

    suspend fun isDuplicateEamil(email: String): Result<IsDuplicateInfo?>

    suspend fun isDuplicatePhoneNum(phoneNum: String): Result<IsDuplicateInfo?>

}
