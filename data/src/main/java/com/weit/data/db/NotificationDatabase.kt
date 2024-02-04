package com.weit.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.weit.data.model.notification.Notification

@Database(
    entities = [Notification::class],
    version = 1,
    exportSchema = false,
)
abstract class NotificationDatabase : RoomDatabase() {
    abstract fun notificationDao(): NotificationDao
}
