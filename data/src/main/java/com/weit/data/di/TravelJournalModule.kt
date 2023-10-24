package com.weit.data.di

import com.weit.data.repository.journal.TravelJournalRepositoryImpl
import com.weit.data.service.TravelJournalService
import com.weit.data.source.TravelJournalDataSource
import com.weit.domain.repository.journal.TravelJournalRepository
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
    fun provideJournalRepository(dataSource: TravelJournalDataSource): TravelJournalRepository =
        TravelJournalRepositoryImpl(dataSource)

    @ActivityRetainedScoped
    @Provides
    fun provideJournalDataSource(service: TravelJournalService): TravelJournalDataSource =
        TravelJournalDataSource(service)

    @ActivityRetainedScoped
    @Provides
    fun provideJournalService(@AuthNetworkObject retrofit: Retrofit): TravelJournalService =
        retrofit.create(TravelJournalService::class.java)
}
