package com.weit.domain.repository.auth

import com.weit.domain.model.auth.UserToken

interface LoginRepository {
    suspend fun loginWithKakao(): Result<UserToken>
}
