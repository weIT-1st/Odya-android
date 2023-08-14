package com.weit.domain.usecase.auth

import com.weit.domain.repository.auth.AuthRepository
import javax.inject.Inject

class IsDuplicateNicknameUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(nickname: String): Boolean {
        var IsDuplicate = false

        authRepository.isDuplicateNickname(nickname).onSuccess {
            IsDuplicate = true
        }.onFailure {
            IsDuplicate = false
        }
        return IsDuplicate
    }
}
