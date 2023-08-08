package com.weit.domain.usecase.login

import com.weit.domain.repository.login.GetUserNameRepository
import javax.inject.Inject

class SetUserNameUsecase @Inject constructor(
    private val getUserNameRepository: GetUserNameRepository
) {
    suspend operator fun invoke(username: String) =
        getUserNameRepository.setUsername(username)
}