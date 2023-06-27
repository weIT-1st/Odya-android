package com.weit.domain.usecase.user

import com.weit.domain.repository.user.UserRepository
import javax.inject.Inject

class UpdateUserEamilUseCase@Inject constructor(
    private val userRepository: UserRepository
) {
    suspend fun updateEmail(newEmail : String) : Result<Unit> =
        userRepository.updateEmail(newEmail)
}