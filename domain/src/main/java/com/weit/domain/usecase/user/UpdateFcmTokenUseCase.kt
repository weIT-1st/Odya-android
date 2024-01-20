package com.weit.domain.usecase.user

import com.weit.domain.repository.user.UserRepository
import javax.inject.Inject

class UpdateFcmTokenUseCase @Inject constructor(
    private val repository: UserRepository,
) {
    suspend operator fun invoke(fcmToken: String): Result<Unit> =
        repository.updateFcmToken(fcmToken)
}
