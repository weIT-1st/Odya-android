package com.weit.presentation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.orhanobut.logger.Logger
import com.weit.domain.model.notification.NotificationInfo
import com.weit.domain.model.user.search.UserProfileColorInfo
import com.weit.domain.model.user.search.UserProfileInfo
import com.weit.domain.usecase.notification.GetNotificationsUseCase
import com.weit.domain.usecase.notification.InsertNotificationUseCase
import com.weit.domain.usecase.notification.UpdateFcmTokenUseCase
import com.weit.presentation.model.NotificationType
import com.weit.presentation.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class FirebaseMessageService : FirebaseMessagingService() {

    @Inject
    lateinit var updateFcmTokenUseCase: UpdateFcmTokenUseCase

    @Inject
    lateinit var insertNotificationUseCase: InsertNotificationUseCase

    private val scope = CoroutineScope(Dispatchers.Main)

    private val notificationManager: NotificationManager by lazy {
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).apply {
            val channel = NotificationChannel(
                getString(R.string.app_name),
                getString(R.string.app_name),
                NotificationManager.IMPORTANCE_HIGH
            )
            createNotificationChannel(channel)
        }
    }
    override fun onNewToken(token: String) {
        scope.launch {
            updateFcmTokenUseCase(token)
        }
    }

    private fun getNotificationInfo(message: RemoteMessage) : NotificationInfo{

        val type = message.data["eventType"]
        val contentId =
            when (type) {
                NotificationType.FOLLOWING_COMMUNITY.name -> {
                    message.data["communityId"]?.toLong()
                }
                NotificationType.COMMUNITY_COMMENT.name -> {
                    message.data["communityId"]?.toLong()
                }
                NotificationType.COMMUNITY_LIKE.name -> {
                    message.data["communityId"]?.toLong()
                }
                NotificationType.FOLLOWING_TRAVEL_JOURNAL.name -> {
                    message.data["travelJournalId"]?.toLong()
                }
                NotificationType.TRAVEL_JOURNAL_TAG.name -> {
                    message.data["travelJournalId"]?.toLong()
                }
                else -> {
                    0L
                }
        }

        val noti = NotificationInfo(
            generateRandomId(),
            contentId,
            message.data["userName"] ?: "",
            message.data["eventType"] ?: "",
            message.data["content"] ?: "",
            UserProfileInfo(
                message.data["userProfileUrl"] ?: "",
                UserProfileColorInfo(
                    message.data["profileColorHex"] ?: "",
                    message.data["profileColorRed"]?.toInt() ?: 0,
                    message.data["profileColorGreen"]?.toInt() ?: 0,
                    message.data["profileColorBlue"]?.toInt() ?: 0
                )
            ),
            message.data["contentImage"]?: "",
            message.data["notifiedAt"]?: ""
        )
        return noti
    }

    private fun setNoti(message: RemoteMessage){
        scope.launch {
            val noti = getNotificationInfo(message)
            withContext(Dispatchers.IO) {
                insertNotificationUseCase(noti)
            }
        }

    }

    private fun handleDataMessage(message: RemoteMessage) {
        setNoti(message)
        showNotification(message)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        if (message.data.isNotEmpty()) {
            handleDataMessage(message)
        }
    }

    private fun showNotification(message: RemoteMessage) {
        val builder = NotificationCompat.Builder(this, getString(R.string.app_name))
            .setSmallIcon(R.drawable.ic_logo_black)
            .setContentTitle(message.data["title"])
            .setContentText(message.data["body"])
            .setContentIntent(createPendingIntent(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        notificationManager.notify(0, builder.build())
    }

    private fun createPendingIntent(message: RemoteMessage): PendingIntent {

        val type = message.data["eventType"]

        val deepLinkBuilder = NavDeepLinkBuilder(this)
            .setComponentName(MainActivity::class.java)
            .setGraph(R.navigation.main_graph)

        when (type) {
            NotificationType.FOLLOWING_COMMUNITY.name -> {
                val arguments = Bundle().apply {
                    putString("communityId", message.data["communityId"])
                    putString("userName", message.data["userName"])
                }
                deepLinkBuilder.apply {
                    setArguments(arguments)
                    addDestination(R.id.fragment_feed_detail)
                }
            }
            NotificationType.COMMUNITY_COMMENT.name -> {
                val arguments = Bundle().apply {
                    putString("communityId", message.data["communityId"])
                    putString("userName", message.data["userName"])
                }
                deepLinkBuilder.apply {
                    setArguments(arguments)
                    addDestination(R.id.fragment_feed_detail)
                }
            }
            NotificationType.COMMUNITY_LIKE.name -> {
                val arguments = Bundle().apply {
                    putString("communityId", message.data["communityId"])
                    putString("userName", message.data["userName"])
                }
                deepLinkBuilder.apply {
                    setArguments(arguments)
                    addDestination(R.id.fragment_feed_detail)
                }
            }
            NotificationType.FOLLOWING_TRAVEL_JOURNAL.name -> {
                val arguments = Bundle().apply {
                    putString("journalId", message.data["travelJournalId"])
                }
                deepLinkBuilder.apply {
                    setArguments(arguments)
                    addDestination(R.id.fragment_travel_Journal)
                }
            }
            NotificationType.TRAVEL_JOURNAL_TAG.name -> {
                val arguments = Bundle().apply {
                    putString("journalId", message.data["travelJournalId"])
                }
                deepLinkBuilder.apply {
                    setArguments(arguments)
                    addDestination(R.id.fragment_travel_Journal)
                }
            }
            NotificationType.FOLLOWER_ADD.name -> {
                val arguments = Bundle().apply {
                    putString("userName", message.data["userName"])
                }
                deepLinkBuilder.apply {
                    setArguments(arguments)
                    addDestination(R.id.otherProfileFragment)
                }
            }
        }

        return deepLinkBuilder.createPendingIntent()
    }

    private fun generateRandomId(): String {
        val randomUUID = UUID.randomUUID()
        return randomUUID.toString().replace("-", "").toUpperCase()
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }
}