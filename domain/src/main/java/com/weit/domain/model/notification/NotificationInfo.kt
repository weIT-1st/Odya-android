package com.weit.domain.model.notification

import com.weit.domain.model.user.search.UserProfileInfo


data class NotificationInfo (
    val notificationId: Long,
    val contentId: Long,
    val userName: String,
    val eventType: String,
    val commentContent: String?="",
    val profile: UserProfileInfo,
    val contentImage: String,
    val notifiedAt: String,
)