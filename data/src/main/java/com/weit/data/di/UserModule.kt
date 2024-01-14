package com.weit.data.di

import android.content.Context
import androidx.room.Room
import com.weit.data.db.ProfileSearchDatabase
import com.weit.data.repository.user.UserSearchRepositoryImpl
import com.weit.data.source.UserSearchDataSource
import com.weit.domain.repository.user.UserSearchRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UserModule {
    @Singleton
    @Provides
    fun provideProfileSearchDatabase(@ApplicationContext context: Context): ProfileSearchDatabase =
        Room.databaseBuilder(
            context.applicationContext,
            ProfileSearchDatabase::class.java,
            "user-database",
        ).build()

    @Singleton
    @Provides
    fun provideUserRepository(
        dataSource: UserSearchDataSource,
    ): UserSearchRepository =
        UserSearchRepositoryImpl(dataSource)

    @Singleton
    @Provides
    fun provideUserSearchDataSource(
        database: ProfileSearchDatabase,
    ): UserSearchDataSource =
        UserSearchDataSource(database)
}
