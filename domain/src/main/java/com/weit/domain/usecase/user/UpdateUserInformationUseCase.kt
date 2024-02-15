package com.weit.domain.usecase.user

import com.weit.domain.model.user.User
import com.weit.domain.repository.user.UserRepository
import javax.inject.Inject

class UpdateUserInformationUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(nickname: String): Result<Unit> =
        userRepository.updateInformation(nickname)
}
