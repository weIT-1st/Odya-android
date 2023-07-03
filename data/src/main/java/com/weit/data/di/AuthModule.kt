package com.weit.data.di

import com.weit.data.repository.auth.AuthRepositoryImpl
import com.weit.data.service.AuthService
import com.weit.data.source.AuthDataSource
import com.weit.domain.repository.auth.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Singleton
    @Provides
    fun providesAuthRepository(
        authDataSource: AuthDataSource,
    ): AuthRepository = AuthRepositoryImpl(authDataSource)

    @Singleton
    @Provides
    fun providesAuthService(
        retrofit: Retrofit,
    ): AuthService = retrofit.create(AuthService::class.java)
}
