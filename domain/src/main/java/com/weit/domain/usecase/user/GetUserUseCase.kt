package com.weit.domain.usecase.user

import com.weit.domain.model.user.User
import com.weit.domain.repository.user.UserRepository
import javax.inject.Inject

class GetUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend fun getUser(): Result<User> =
        userRepository.getUser()
}