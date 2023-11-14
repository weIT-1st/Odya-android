package com.weit.data.di

import com.weit.data.repository.bookmark.BookMarkRepositoryImpl
import com.weit.data.service.BookMarkService
import com.weit.data.source.BookMarkDataSource
import com.weit.domain.repository.bookmark.BookMarkRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import retrofit2.Retrofit
import retrofit2.create

@Module
@InstallIn(ActivityRetainedComponent::class)
class BookMarkModule {

    @ActivityRetainedScoped
    @Provides
    fun provideBookMarkRepository(dataSource: BookMarkDataSource): BookMarkRepository =
        BookMarkRepositoryImpl(dataSource)

    @ActivityRetainedScoped
    @Provides
    fun provideBookMarkDataSource(service: BookMarkService): BookMarkDataSource =
        BookMarkDataSource(service)

    @ActivityRetainedScoped
    @Provides
    fun provideBookMarkService(@AuthNetworkObject retrofit: Retrofit): BookMarkService =
        retrofit.create(BookMarkService::class.java)
}
