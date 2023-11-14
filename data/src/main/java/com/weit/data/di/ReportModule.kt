package com.weit.data.di

import com.weit.data.service.ReportService
import com.weit.data.source.ReportDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import retrofit2.Retrofit
import retrofit2.create

@Module
@InstallIn(ActivityRetainedComponent::class)
class ReportModule {

    @ActivityRetainedScoped
    @Provides
    fun proivdeReportRepository(dataSource: ReportDataSource): ReportRepository =
        ReportRepositoryImpl(dataSource)

    @ActivityRetainedScoped
    @Provides
    fun provideReportDataSource(service: ReportService): ReportDataSource =
        ReportDataSource(service)

    @ActivityRetainedScoped
    @Provides
    fun provideReportService(@AuthNetworkObject retrofit: Retrofit): ReportService =
        retrofit.create(ReportService::class.java)
}
