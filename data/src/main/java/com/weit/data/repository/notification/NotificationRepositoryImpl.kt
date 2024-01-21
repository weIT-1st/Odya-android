package com.weit.data.repository.notification

import com.weit.data.model.coordinate.Coordinate
import com.weit.data.model.notification.Notification
import com.weit.data.model.user.search.UserSearch
import com.weit.data.model.user.search.UserSearchProfile
import com.weit.data.model.user.search.UserSearchProfileColor
import com.weit.data.source.CoordinateDataSource
import com.weit.data.source.NotificationDataSource
import com.weit.data.source.UserSearchDataSource
import com.weit.domain.model.CoordinateInfo
import com.weit.domain.model.CoordinateTimeInfo
import com.weit.domain.model.notification.NotificationInfo
import com.weit.domain.model.user.search.UserProfileColorInfo
import com.weit.domain.model.user.search.UserProfileInfo
import com.weit.domain.model.user.search.UserSearchInfo
import com.weit.domain.repository.coordinate.CoordinateRepository
import com.weit.domain.repository.notification.NotificationRepository
import com.weit.domain.repository.user.UserSearchRepository
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val dataSource: NotificationDataSource,
) : NotificationRepository {

    private fun NotificationInfo.toNotification(): Notification =
        Notification(
            notificationId,
            contentId,
            userName,
            eventType,
            commentContent,
            UserSearchProfile(
                profile.url,
                profile.color?.let {
                    UserSearchProfileColor(
                        it.colorHex,
                        it.red,
                        it.green,
                        it.blue
                    )
                }
            ),
            contentImage,
            notifiedAt,
            System.currentTimeMillis(),
        )

    override suspend fun insertNotification(noti: NotificationInfo) {
        dataSource.insertNotification(noti.toNotification())
    }

    override suspend fun deleteNotification(id: Long) {
        dataSource.deleteNotification(id)
    }

    override suspend fun getNotification(): List<NotificationInfo> {
        val result = dataSource.getNotifications()
        return result.map {
            NotificationInfo(it.notificationId,it.contentId,
                it.userName,it.eventType,
                it.commentContent,
                UserProfileInfo(it.profile.profileUrl,
                    it.profile.profileColor?.let {
                        UserProfileColorInfo(
                            it.colorHex,
                            it.red,
                            it.green,
                            it.blue)
                    }
                ),
                it.contentImage,
                it.notifiedAt
            )
        }
    }
}
