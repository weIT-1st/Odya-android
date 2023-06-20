package com.weit.data.di

import android.content.Context
import com.weit.data.repository.auth.AuthRepositoryImpl
import com.weit.domain.repository.auth.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Singleton
    @Provides
    fun providesAuthRepository(
        @ApplicationContext context: Context,
    ): AuthRepository = AuthRepositoryImpl(context)
}
