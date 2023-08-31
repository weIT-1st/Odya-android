package com.weit.data.di

import android.content.Context
import androidx.room.Room
import com.weit.data.db.CoordinateDatabase
import com.weit.data.repository.coordinate.CoordinateRepositoryImpl
import com.weit.data.source.CoordinateDataSource
import com.weit.domain.repository.coordinate.CoordinateRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class CoordinateModule {
    @Singleton
    @Provides
    fun provideCoordinateDatabase(@ApplicationContext context: Context): CoordinateDatabase =
        Room.databaseBuilder(
            context.applicationContext,
            CoordinateDatabase::class.java,
            "coordinate-database",
        ).build()

    @Singleton
    @Provides
    fun provideCoordinateRepository(
        dataSource: CoordinateDataSource,
    ): CoordinateRepository =
        CoordinateRepositoryImpl(dataSource)

    @Singleton
    @Provides
    fun provideCoordinateDataSource(
        database: CoordinateDatabase,
    ): CoordinateDataSource =
        CoordinateDataSource(database)
}
