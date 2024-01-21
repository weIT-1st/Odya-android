package com.weit.data.source

import com.weit.data.db.NotificationDatabase
import com.weit.data.db.ProfileSearchDatabase
import com.weit.data.model.notification.Notification
import com.weit.data.model.user.search.UserSearch
import javax.inject.Inject

class NotificationDataSource @Inject constructor(
    private val db: NotificationDatabase,
) {
    suspend fun insertNotification(notification: Notification) {
        db.notificationDao().insertNotification(notification)
    }

    suspend fun deleteNotification(notificationId: Long) {
        db.notificationDao().deleteNotification(notificationId)
    }

    suspend fun getNotifications(): List<Notification> {
        return db.notificationDao().getNotifications()
    }
}
