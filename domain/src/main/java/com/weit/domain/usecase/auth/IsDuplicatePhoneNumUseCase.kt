package com.weit.domain.usecase.auth

import com.weit.domain.model.auth.IsDuplicateInfo
import com.weit.domain.repository.auth.AuthRepository
import javax.inject.Inject

class IsDuplicatePhoneNumUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend fun invoke(phoneNum: String) : Result<IsDuplicateInfo?> =
        authRepository.isDuplicatePhoneNum(phoneNum)
}