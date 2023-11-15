package com.weit.data.di

import android.content.Context
import com.weit.data.repository.place.PlaceSearchHistoryRepositoryImpl
import com.weit.data.service.PlaceSearchHistoryService
import com.weit.data.source.PlaceSearchHistoryDataSource
import com.weit.domain.repository.place.PlaceSearchHistoryRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(ActivityRetainedComponent::class)
class PlaceSearchHistoryModule {
    @ActivityRetainedScoped
    @Provides
    fun providePlaceSearchHistoryRepository(dataSource: PlaceSearchHistoryDataSource): PlaceSearchHistoryRepository =
        PlaceSearchHistoryRepositoryImpl(dataSource)

    @ActivityRetainedScoped
    @Provides
    fun providePlaceSearchHistoryDataSource(
        @ApplicationContext context: Context,
        service: PlaceSearchHistoryService
    ): PlaceSearchHistoryDataSource =
        PlaceSearchHistoryDataSource(context, service)

    @ActivityRetainedScoped
    @Provides
    fun providePlaceSearchHistoryService(@AuthNetworkObject retrofit: Retrofit): PlaceSearchHistoryService =
        retrofit.create(PlaceSearchHistoryService::class.java)

}
