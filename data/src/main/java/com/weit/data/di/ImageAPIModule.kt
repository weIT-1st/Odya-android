package com.weit.data.di

import com.weit.data.repository.image.ImageAPIRepositoryImpl
import com.weit.data.service.ImageAPIService
import com.weit.data.source.ImageAPIDataSource
import com.weit.domain.repository.image.ImageAPIRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.scopes.ActivityRetainedScoped
import retrofit2.Retrofit
import retrofit2.create

@Module
@InstallIn(ActivityRetainedScoped::class)
class ImageAPIModule {

    @ActivityRetainedScoped
    @Provides
    fun provideImageAPIService(@AuthNetworkObject retrofit: Retrofit): ImageAPIService =
        retrofit.create(ImageAPIService::class.java)

    @ActivityRetainedScoped
    @Provides
    fun provideImageAPIRepository(dataSource: ImageAPIDataSource): ImageAPIRepository =
        ImageAPIRepositoryImpl(dataSource)

    @ActivityRetainedScoped
    @Provides
    fun provideImageAPIDataSource(service: ImageAPIService): ImageAPIDataSource =
        ImageAPIDataSource(service)
}
