package com.weit.domain.usecase.user

import com.weit.domain.repository.user.UserRepository
import javax.inject.Inject

class UpdateUserInformationUseCase@Inject constructor(
    private val userRepository: UserRepository
) {
    suspend fun updateInformation(newNickName: String) : Result<Unit> =
        userRepository.updateInformation(newNickName)
}