package com.weit.domain.usecase.user

import com.weit.domain.repository.user.UserRepository
import javax.inject.Inject

class UpdateProfileUseCase@Inject constructor(
    private val userRepository: UserRepository,
) {
    suspend fun updateProfile(uri: String): Result<Unit> =
        userRepository.updateProfile(uri)
}
