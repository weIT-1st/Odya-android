package com.weit.domain.usecase.notification

import com.weit.domain.model.CoordinateInfo
import com.weit.domain.model.notification.NotificationInfo
import com.weit.domain.model.user.search.UserSearchInfo
import com.weit.domain.repository.coordinate.CoordinateRepository
import com.weit.domain.repository.notification.NotificationRepository
import com.weit.domain.repository.user.UserSearchRepository
import javax.inject.Inject

class InsertNotificationUseCase @Inject constructor(
    private val repository: NotificationRepository,
) {
    suspend operator fun invoke(noti: NotificationInfo) =
        repository.insertNotification(noti)
}
