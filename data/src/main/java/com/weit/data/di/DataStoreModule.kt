package com.weit.data.di

import android.content.Context
import com.weit.data.repository.userinfo.UserInfoRepositoryImpl
import com.weit.data.source.UserInfoDataSource
import com.weit.domain.repository.userinfo.UserInfoRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataStoreModule {

    @Provides
    @Singleton
    fun provideUserInfoRepository(dataSource: UserInfoDataSource): UserInfoRepository =
        UserInfoRepositoryImpl(dataSource)

    @Provides
    @Singleton
    fun provideUserInfoDataSource(@ApplicationContext context: Context): UserInfoDataSource =
        UserInfoDataSource(context)
}
