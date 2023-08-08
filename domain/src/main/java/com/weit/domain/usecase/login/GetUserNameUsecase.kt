package com.weit.domain.usecase.login

import com.weit.domain.repository.login.GetUserNameRepository
import javax.inject.Inject

class GetUserNameUsecase @Inject constructor(
    private val getUserNameRepository: GetUserNameRepository
) {
    suspend operator fun invoke(): Result<String?> =
        getUserNameRepository.getUsername()
}