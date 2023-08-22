package com.weit.domain.usecase.auth

import com.weit.domain.repository.auth.AuthRepository
import javax.inject.Inject

class IsDuplicateNicknameUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(nickname: String): Boolean {

        return authRepository.isDuplicateNickname(nickname).isFailure
    }
}
