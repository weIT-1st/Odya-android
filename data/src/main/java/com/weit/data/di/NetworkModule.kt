package com.weit.data.di

import com.squareup.moshi.Moshi
import com.weit.data.BuildConfig
import com.weit.data.interceptor.AuthInterceptor
import com.weit.data.source.AuthDataSource
import com.weit.data.util.LocalDateTimeConverter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

private const val BASE_URL = "https://jayden-bin.kro.kr/"

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class NormalNetworkObject

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthNetworkObject

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @NormalNetworkObject
    @Singleton
    @Provides
    fun provideNormalOkHttpClient(): OkHttpClient =
        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                setLevel(HttpLoggingInterceptor.Level.BODY)
            }
            OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()
        } else {
            OkHttpClient.Builder()
                .build()
        }

    @NormalNetworkObject
    @Singleton
    @Provides
    fun provideNormalRetrofit(
        @NormalNetworkObject okHttpClient: OkHttpClient,
        moshi: Moshi,
    ): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @AuthNetworkObject
    @Singleton
    @Provides
    fun provideAuthOkHttpClient(
        authInterceptor: AuthInterceptor,
    ): OkHttpClient =
        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                setLevel(HttpLoggingInterceptor.Level.BODY)
            }
            OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .addInterceptor(loggingInterceptor)
                .build()
        } else {
            OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .build()
        }

    @AuthNetworkObject
    @Singleton
    @Provides
    fun provideAuthRetrofit(
        @AuthNetworkObject okHttpClient: OkHttpClient,
        moshi: Moshi,
    ): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Singleton
    @Provides
    fun providesAuthInterceptor(
        dataSource: AuthDataSource,
    ): AuthInterceptor = AuthInterceptor(dataSource)

    @Singleton
    @Provides
    fun provideMoshi(): Moshi = Moshi.Builder()
        .add(LocalDateTimeConverter())
        .build()
}
