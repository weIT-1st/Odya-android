package com.weit.data.model.notification

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.weit.data.model.user.search.UserSearchProfile
import com.weit.domain.model.user.search.UserProfileInfo

@Entity(tableName = "notification")
data class Notification(
    @PrimaryKey
    val notificationId: String,
    val contentId: Long?=0L,
    val userName: String,
    val eventType: String,
    val commentContent: String?="",
    @Embedded
    val profile: UserSearchProfile,
    val contentImage: String?="",
    val notifiedAt: String,
    val time: Long
)
