package com.weit.domain.model.notification

import com.weit.domain.model.user.search.UserProfileInfo


data class NotificationInfo (
    val notificationId: String,
    val contentId: Long?=0L,
    val userName: String,
    val eventType: String,
    val commentContent: String?="",
    val profile: UserProfileInfo,
    val contentImage: String?="",
    val notifiedAt: String,
)