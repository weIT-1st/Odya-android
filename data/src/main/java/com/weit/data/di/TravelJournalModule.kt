package com.weit.data.di

import com.squareup.moshi.Moshi
import com.weit.data.repository.journal.TravelJournalRepositoryImpl
import com.weit.data.service.TravelJournalService
import com.weit.data.source.ImageDataSource
import com.weit.data.source.TravelJournalDataSource
import com.weit.domain.repository.image.ImageRepository
import com.weit.domain.repository.journal.TravelJournalRepository
import com.weit.domain.repository.place.PlaceRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import retrofit2.Retrofit

@Module
@InstallIn(ActivityRetainedComponent::class)
class TravelJournalModule {

    @ActivityRetainedScoped
    @Provides
    fun provideJournalService(@AuthNetworkObject retrofit: Retrofit): TravelJournalService =
        retrofit.create(TravelJournalService::class.java)

    @ActivityRetainedScoped
    @Provides
    fun provideJournalRepository(
        travelJournalDataSource: TravelJournalDataSource,
        imageRepository: ImageRepository,
        imageDataSource: ImageDataSource,
        placeRepository: PlaceRepository,
        moshi: Moshi
    ): TravelJournalRepository =
        TravelJournalRepositoryImpl(travelJournalDataSource, imageRepository, imageDataSource, placeRepository, moshi)

    @ActivityRetainedScoped
    @Provides
    fun provideJournalDataSource(service: TravelJournalService): TravelJournalDataSource =
        TravelJournalDataSource(service)
}
