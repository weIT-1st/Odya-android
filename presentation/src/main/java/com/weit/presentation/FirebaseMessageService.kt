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
import com.weit.domain.usecase.user.UpdateFcmTokenUseCase
import com.weit.presentation.model.NotificationType
import com.weit.presentation.ui.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


class FirebaseMessageService : FirebaseMessagingService() {

    @Inject
    lateinit var updateFcmTokenUseCase: UpdateFcmTokenUseCase

    private val scope = CoroutineScope(Dispatchers.IO)

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

    override fun onMessageReceived(message: RemoteMessage) {
        val builder = NotificationCompat.Builder(this, getString(R.string.app_name))
            .setSmallIcon(R.drawable.ic_logo_black)
            .setContentTitle(message.notification?.title)
            .setContentText(message.notification?.body)
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
                    putString("travelJournalId", message.data["travelJournalId"])
                }
                deepLinkBuilder.apply {
                    setArguments(arguments)
//                    addDestination(R.id.fragment_feed_detail)
                }
            }
            NotificationType.TRAVEL_JOURNAL_TAG.name -> {
                val arguments = Bundle().apply {
                    putString("travelJournalId", message.data["travelJournalId"])
                }
                deepLinkBuilder.apply {
                    setArguments(arguments)
//                    addDestination(R.id.fragment_feed_detail)
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


}