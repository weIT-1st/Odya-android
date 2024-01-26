package com.weit.domain.repository.notification

import com.weit.domain.model.notification.NotificationInfo

interface NotificationRepository {
    suspend fun updateFcmToken(fcmToken: String): Result<Unit>

    suspend fun insertNotification(noti: NotificationInfo)

    suspend fun deleteNotification(id: Long)

    suspend fun getNotification(): List<NotificationInfo>

}
