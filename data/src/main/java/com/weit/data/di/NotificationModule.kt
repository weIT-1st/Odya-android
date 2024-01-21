package com.weit.data.di

import android.content.Context
import androidx.room.Room
import com.weit.data.db.NotificationDatabase
import com.weit.data.db.ProfileSearchDatabase
import com.weit.data.repository.notification.NotificationRepositoryImpl
import com.weit.data.repository.user.UserSearchRepositoryImpl
import com.weit.data.source.NotificationDataSource
import com.weit.data.source.UserSearchDataSource
import com.weit.domain.repository.notification.NotificationRepository
import com.weit.domain.repository.user.UserSearchRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NotificationModule {
    @Singleton
    @Provides
    fun provideNotificationDatabase(@ApplicationContext context: Context): NotificationDatabase =
        Room.databaseBuilder(
            context.applicationContext,
            NotificationDatabase::class.java,
            "notification-database",
        ).build()

    @Singleton
    @Provides
    fun provideNotificationRepository(
        dataSource: NotificationDataSource,
    ): NotificationRepository =
        NotificationRepositoryImpl(dataSource)

    @Singleton
    @Provides
    fun provideNotificationDataSource(
        database: NotificationDatabase,
    ): NotificationDataSource =
        NotificationDataSource(database)
}
