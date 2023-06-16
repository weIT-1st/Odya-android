package com.weit.data.di

import android.content.Context
import com.weit.data.repository.auth.LoginRepositoryImpl
import com.weit.domain.repository.auth.LoginRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
class LoginModule {
    @ActivityScoped
    @Provides
    fun provideLoginRepository(
        @ActivityContext context: Context,
    ): LoginRepository = LoginRepositoryImpl(context)
}
