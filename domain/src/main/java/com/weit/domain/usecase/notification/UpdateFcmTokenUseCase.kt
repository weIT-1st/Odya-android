package com.weit.domain.usecase.notification

import com.weit.domain.repository.notification.NotificationRepository
import javax.inject.Inject

class UpdateFcmTokenUseCase @Inject constructor(
    private val repository: NotificationRepository,
) {
    suspend operator fun invoke(fcmToken: String): Result<Unit> =
        repository.updateFcmToken(fcmToken)
}
