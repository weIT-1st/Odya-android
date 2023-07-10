package com.weit.data.di

import com.weit.data.repository.follow.FollowRepositoryImpl
import com.weit.data.service.FollowService
import com.weit.data.source.FollowDataSource
import com.weit.domain.repository.follow.FollowRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FollowModule {

    @Singleton
    @Provides
    fun provideFollowService(retrofit: Retrofit): FollowService =
        retrofit.create(FollowService::class.java)

    @Singleton
    @Provides
    fun provideFollowDataSource(followService: FollowService): FollowDataSource =
        FollowDataSource(followService)

    @Singleton
    @Provides
    fun provideFollowRepository(followDataSource: FollowDataSource) : FollowRepository =
        FollowRepositoryImpl(followDataSource)
}
