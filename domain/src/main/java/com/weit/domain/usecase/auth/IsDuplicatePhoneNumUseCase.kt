package com.weit.domain.usecase.auth

import com.weit.domain.repository.auth.AuthRepository
import java.io.Serializable
import javax.inject.Inject

class IsDuplicatePhoneNumUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(phoneNum: String) : Boolean{
        var IsDuplicate = false

        authRepository.isDuplicatePhoneNum(phoneNum).onSuccess {
            IsDuplicate = true
        }.onFailure {
            IsDuplicate = false
        }
        return IsDuplicate
    }
}
