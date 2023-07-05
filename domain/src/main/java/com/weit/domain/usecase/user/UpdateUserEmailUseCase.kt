package com.weit.domain.usecase.user

import com.weit.domain.model.user.User
import com.weit.domain.repository.user.UserRepository
import javax.inject.Inject

class UpdateUserEmailUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    suspend fun updateEmail(emailUpdateUser: User): Result<Unit> =
        userRepository.updateEmail(emailUpdateUser)
}
