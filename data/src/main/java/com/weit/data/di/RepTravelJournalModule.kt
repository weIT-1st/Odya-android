package com.weit.data.di

import com.squareup.moshi.Moshi
import com.weit.data.repository.journal.TravelJournalRepositoryImpl
import com.weit.data.repository.repjournal.RepTravelJournalRepositoryImpl
import com.weit.data.service.RepTravelJournalService
import com.weit.data.service.TravelJournalService
import com.weit.data.source.ImageDataSource
import com.weit.data.source.RepTravelJournalDataSource
import com.weit.data.source.TravelJournalDataSource
import com.weit.domain.repository.image.ImageRepository
import com.weit.domain.repository.journal.TravelJournalRepository
import com.weit.domain.repository.repjournal.RepTravelJournalRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import retrofit2.Retrofit

@Module
@InstallIn(ActivityRetainedComponent::class)
class RepTravelJournalModule {

    @ActivityRetainedScoped
    @Provides
    fun provideRepJournalService(@AuthNetworkObject retrofit: Retrofit): RepTravelJournalService =
        retrofit.create(RepTravelJournalService::class.java)

    @ActivityRetainedScoped
    @Provides
    fun provideRepJournalRepository(
        repTravelJournalDataSource: RepTravelJournalDataSource,
    ): RepTravelJournalRepository =
        RepTravelJournalRepositoryImpl(repTravelJournalDataSource)

    @ActivityRetainedScoped
    @Provides
    fun provideRepJournalDataSource(service: RepTravelJournalService): RepTravelJournalDataSource =
        RepTravelJournalDataSource(service)
}
