package com.weit.data.di

import com.weit.data.repository.user.UserRepositoryImpl
import com.weit.data.service.UserService
import com.weit.data.source.ImageDataSource
import com.weit.data.source.UserDataSource
import com.weit.domain.repository.user.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UserModule {

    @Singleton
    @Provides
    fun provideUserService(@AuthNetworkObject retrofit: Retrofit): UserService =
        retrofit.create(UserService::class.java)

    @Singleton
    @Provides
    fun provideUserDataSource(userService: UserService): UserDataSource =
        UserDataSource(userService)

    @Singleton
    @Provides
    fun provideUserRepository(
        userDataSource: UserDataSource,
        imageDataSource: ImageDataSource
    ): UserRepository =
        UserRepositoryImpl(userDataSource, imageDataSource)
}
