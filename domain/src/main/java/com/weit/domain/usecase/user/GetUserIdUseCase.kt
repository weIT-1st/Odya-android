package com.weit.domain.usecase.user

import com.weit.domain.repository.user.UserRepository
import javax.inject.Inject

class GetUserIdUseCase @Inject constructor(
    private val repository: UserRepository,
) {
    suspend operator fun invoke(): Int =
        repository.getUserId()
}
