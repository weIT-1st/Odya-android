package com.weit.domain.usecase.auth

import com.weit.domain.repository.auth.AuthRepository
import javax.inject.Inject

class IsDuplicatePhoneNumUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(phoneNum: String): Result<Unit> =
        authRepository.isDuplicatePhoneNum(phoneNum)
}
