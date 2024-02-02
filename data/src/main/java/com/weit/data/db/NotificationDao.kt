package com.weit.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.weit.data.model.notification.Notification
import com.weit.data.model.user.search.UserSearch

@Dao
interface NotificationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: Notification)

    @Query("DELETE FROM notification WHERE notificationId=:notificationId")
    suspend fun deleteNotification(notificationId: Long)

    @Query("SELECT * FROM notification ORDER BY time DESC")
    suspend fun getNotifications(): List<Notification>
}
