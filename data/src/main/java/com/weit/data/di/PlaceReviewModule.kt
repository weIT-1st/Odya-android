package com.weit.data.di

import com.weit.data.repository.place.PlaceReviewRepositoryImpl
import com.weit.data.service.PlaceReviewService
import com.weit.data.source.PlaceReviewDateSource
import com.weit.domain.repository.place.PlaceReviewRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class PlaceReviewModule {

    @Singleton
    @Provides
    fun providePlaceReviewService(retrofit: Retrofit): PlaceReviewService =
        retrofit.create(PlaceReviewService::class.java)

    @Singleton
    @Provides
    fun providePlaceReviewDataSource(service: PlaceReviewService): PlaceReviewDateSource =
        PlaceReviewDateSource(service)

    @Singleton
    @Provides
    fun providePlaceReviewRepository(dataSource: PlaceReviewDateSource): PlaceReviewRepository =
        PlaceReviewRepositoryImpl(dataSource)
}
