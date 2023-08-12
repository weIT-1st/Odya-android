package com.weit.data.di

import android.content.Context
import com.weit.data.repository.user.UserRepositoryImpl
import com.weit.data.service.UserService
import com.weit.data.source.UserDataSource
import com.weit.domain.repository.user.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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
    fun provideUserDataSource(
        @ApplicationContext context: Context,
        userService: UserService,
    ): UserDataSource = UserDataSource(context, userService)

    @Singleton
    @Provides
    fun provideUserRepository(userDataSource: UserDataSource): UserRepository =
        UserRepositoryImpl(userDataSource)
}
