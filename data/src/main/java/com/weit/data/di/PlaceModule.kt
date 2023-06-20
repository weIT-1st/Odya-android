package com.weit.data.di

import android.content.Context
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.PlacesClient
import com.weit.data.BuildConfig
import com.weit.data.repository.place.PlaceRepositoryImpl
import com.weit.data.service.PlaceService
import com.weit.data.source.PlaceDateSource
import com.weit.domain.repository.place.PlaceRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class PlaceModule {

    @Singleton
    @Provides
    fun providePlaceClient(@ApplicationContext context: Context): PlacesClient {
        Places.initialize(context, BuildConfig.GOOGLE_MAP_KEY)
        return Places.createClient(context)
    }

    @Singleton
    @Provides
    fun provideAutocompleteSessionToken(): AutocompleteSessionToken {
        return AutocompleteSessionToken.newInstance()
    }

    @Singleton
    @Provides
    fun providePlaceService(retrofit: Retrofit): PlaceService =
        retrofit.create(PlaceService::class.java)

    @Singleton
    @Provides
    fun providePlaceDataSource(service: PlaceService, sessionToken: AutocompleteSessionToken, placesClient: PlacesClient): PlaceDateSource =
        PlaceDateSource(service, sessionToken, placesClient)

    @Singleton
    @Provides
    fun providePlaceRepository(dataSource: PlaceDateSource): PlaceRepository =
        PlaceRepositoryImpl(dataSource)
}
