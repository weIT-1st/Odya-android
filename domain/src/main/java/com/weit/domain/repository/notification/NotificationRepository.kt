package com.weit.domain.repository.notification

import com.weit.domain.model.notification.NotificationInfo

interface NotificationRepository {

    suspend fun insertNotification(noti: NotificationInfo)

    suspend fun deleteNotification(id: Long)

    suspend fun getNotification(): List<NotificationInfo>

}
