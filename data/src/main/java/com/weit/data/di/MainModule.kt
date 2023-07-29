package com.weit.data.di

import android.content.Context
import android.location.LocationManager
import androidx.room.Room
import com.weit.data.db.CoordinateDatabase
import com.weit.data.repository.coordinate.CoordinateRepositoryImpl
import com.weit.data.repository.example.ExampleRepositoryImpl
import com.weit.data.repository.image.ImageRepositoryImpl
import com.weit.data.repository.place.PlaceReviewRepositoryImpl
import com.weit.data.service.ExampleService
import com.weit.data.service.PlaceReviewService
import com.weit.data.source.CoordinateDataSource
import com.weit.data.source.ExampleDataSource
import com.weit.data.source.ImageDataSource
import com.weit.data.source.PlaceReviewDateSource
import com.weit.domain.repository.CoordinateRepository
import com.weit.domain.repository.example.ExampleRepository
import com.weit.domain.repository.image.ImageRepository
import com.weit.domain.repository.place.PlaceReviewRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.qualifiers.ApplicationContext
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
    fun provideExampleService(@NormalNetworkObject retrofit: Retrofit): ExampleService =
        retrofit.create(ExampleService::class.java)

    @ActivityRetainedScoped
    @Provides
    fun provideImageRepository(dataSource: ImageDataSource): ImageRepository =
        ImageRepositoryImpl(dataSource)

    @ActivityRetainedScoped
    @Provides
    fun provideImageDataSource(@ApplicationContext context: Context): ImageDataSource =
        ImageDataSource(context.contentResolver)

    @ActivityRetainedScoped
    @Provides
    fun providePlaceReviewService(@AuthNetworkObject retrofit: Retrofit): PlaceReviewService =
        retrofit.create(PlaceReviewService::class.java)

    @ActivityRetainedScoped
    @Provides
    fun providePlaceReviewDataSource(service: PlaceReviewService): PlaceReviewDateSource =
        PlaceReviewDateSource(service)

    @ActivityRetainedScoped
    @Provides
    fun providePlaceReviewRepository(dataSource: PlaceReviewDateSource): PlaceReviewRepository =
        PlaceReviewRepositoryImpl(dataSource)

    @ActivityRetainedScoped
    @Provides
    fun provideCoordinateDatabase(@ApplicationContext context: Context): CoordinateDatabase =
        Room.databaseBuilder(
            context.applicationContext,
            CoordinateDatabase::class.java,
            "coordinate-database",
        ).build()

    @ActivityRetainedScoped
    @Provides
    fun provideCoordinateRepository(dataSource: CoordinateDataSource): CoordinateRepository =
        CoordinateRepositoryImpl(dataSource)

    @ActivityRetainedScoped
    @Provides
    fun provideCoordinateDataSource(
        database: CoordinateDatabase,
        locationManager: LocationManager,
    ): CoordinateDataSource =
        CoordinateDataSource(database, locationManager)

    @ActivityRetainedScoped
    @Provides
    fun provideLocationManager(@ApplicationContext context: Context): LocationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
}
