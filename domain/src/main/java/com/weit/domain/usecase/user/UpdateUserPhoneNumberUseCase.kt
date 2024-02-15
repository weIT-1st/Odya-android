package com.weit.domain.usecase.user

import com.weit.domain.model.user.User
import com.weit.domain.repository.user.UserRepository
import javax.inject.Inject

class UpdateUserPhoneNumberUseCase@Inject constructor(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(phoneNumber: String): Result<Unit> =
        userRepository.updatePhoneNumber(phoneNumber)
}
