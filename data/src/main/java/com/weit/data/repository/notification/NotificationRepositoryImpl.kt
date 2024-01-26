package com.weit.data.repository.notification

import com.weit.data.model.notification.Notification
import com.weit.data.model.user.FcmToken
import com.weit.data.model.user.search.UserSearchProfile
import com.weit.data.model.user.search.UserSearchProfileColor
import com.weit.data.source.NotificationDataSource
import com.weit.domain.model.exception.InvalidRequestException
import com.weit.domain.model.exception.InvalidTokenException
import com.weit.domain.model.exception.UnKnownException
import com.weit.domain.model.notification.NotificationInfo
import com.weit.domain.model.user.search.UserProfileColorInfo
import com.weit.domain.model.user.search.UserProfileInfo
import com.weit.domain.repository.notification.NotificationRepository
import okhttp3.internal.http.HTTP_BAD_REQUEST
import okhttp3.internal.http.HTTP_UNAUTHORIZED
import retrofit2.Response
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val dataSource: NotificationDataSource,
) : NotificationRepository {
    override suspend fun updateFcmToken(fcmToken: String): Result<Unit> {
        val result = dataSource.updateFcmToken(FcmToken(fcmToken))
        return if (result.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(handleError(result))
        }
    }
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

    private fun handleCode(code: Int): Throwable {
        return when (code) {
            HTTP_BAD_REQUEST -> InvalidRequestException()
            HTTP_UNAUTHORIZED -> InvalidTokenException()
            else -> {
                UnKnownException()
            }
        }
    }

    private fun handleError(response: Response<Unit>): Throwable {
        return handleCode(response.code())
    }
}
