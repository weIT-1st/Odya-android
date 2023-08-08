package com.weit.data.di

import com.weit.data.repository.login.GetUsernameRepositoryImpl
import com.weit.domain.repository.login.GetUserNameRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LoginPreferencesModule{

    @Binds
    @Singleton
    abstract fun bindPreferencesModule(
        getUsernameRepositoryImpl: GetUsernameRepositoryImpl
    ): GetUserNameRepository

}
