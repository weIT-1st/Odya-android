package com.weit.domain.usecase.user

import com.weit.domain.repository.user.UserRepository
import javax.inject.Inject

class UpdateUserPhoneNumberUseCase@Inject constructor(
    private val userRepository: UserRepository
) {
    suspend fun updatePhoneNumber(newPhoneNumber: String): Result<Unit> =
        userRepository.updatePhoneNumber(newPhoneNumber)
}