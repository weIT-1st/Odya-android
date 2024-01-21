package com.weit.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.weit.data.model.user.search.UserSearch
import com.weit.domain.model.notification.NotificationInfo

@Database(
    entities = [NotificationInfo::class],
    version = 1,
    exportSchema = false,
)
abstract class NotificationDatabase : RoomDatabase() {
    abstract fun notificationDao(): NotificationDao
}
