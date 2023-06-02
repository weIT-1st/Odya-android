package com.weit.data.di

import com.weit.data.repository.example.ExampleRepositoryImpl
import com.weit.data.service.ExampleService
import com.weit.data.source.ExampleDataSource
import com.weit.domain.repository.example.ExampleRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import retrofit2.Retrofit

@Module
@InstallIn(ActivityRetainedComponent::class)
class MainModule {

    @ActivityRetainedScoped
    @Provides
    fun provideExampleRepository(dataSource: ExampleDataSource): ExampleRepository =
        ExampleRepositoryImpl(dataSource)

    @ActivityRetainedScoped
    @Provides
    fun provideExampleDataSource(service: ExampleService): ExampleDataSource =
        ExampleDataSource(service)

    @ActivityRetainedScoped
    @Provides
    fun provideExampleService(retrofit: Retrofit): ExampleService =
        retrofit.create(ExampleService::class.java)
}
