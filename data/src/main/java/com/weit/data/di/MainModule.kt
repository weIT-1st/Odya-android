package com.weit.data.di

import android.content.Context
import com.squareup.moshi.Moshi
import com.weit.data.repository.community.CommentRepositoryImpl
import com.weit.data.repository.community.CommunityRepositoryImpl
import com.weit.data.repository.example.ExampleRepositoryImpl
import com.weit.data.repository.favoritePlace.FavoritePlaceRepositoryImpl
import com.weit.data.repository.image.ImageRepositoryImpl
import com.weit.data.repository.place.PlaceReviewRepositoryImpl
import com.weit.data.repository.term.TermRepositoryImpl
import com.weit.data.repository.topic.TopicRepositoryImpl
import com.weit.data.repository.user.UserRepositoryImpl
import com.weit.data.service.CommentService
import com.weit.data.service.CommunityService
import com.weit.data.service.ExampleService
import com.weit.data.service.FavoritePlaceService
import com.weit.data.service.PlaceReviewService
import com.weit.data.service.TermService
import com.weit.data.service.TopicService
import com.weit.data.service.UserService
import com.weit.data.source.CommentDataSource
import com.weit.data.source.CommunityDataSource
import com.weit.data.source.ExampleDataSource
import com.weit.data.source.FavoritePlaceDateSource
import com.weit.data.source.ImageDataSource
import com.weit.data.source.PermissionDataSource
import com.weit.data.source.PlaceReviewDataSource
import com.weit.data.source.TermDataSource
import com.weit.data.source.TopicDataSource
import com.weit.data.source.UserDataSource
import com.weit.data.source.UserInfoDataSource
import com.weit.domain.repository.community.comment.CommentRepository
import com.weit.domain.repository.community.comment.CommunityRepository
import com.weit.domain.repository.example.ExampleRepository
import com.weit.domain.repository.favoritePlace.FavoritePlaceRepository
import com.weit.domain.repository.image.ImageRepository
import com.weit.domain.repository.place.PlaceReviewRepository
import com.weit.domain.repository.term.TermRepository
import com.weit.domain.repository.topic.TopicRepository
import com.weit.domain.repository.user.UserRepository
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
    fun providePlaceReviewService(@AuthNetworkObject retrofit: Retrofit): PlaceReviewService =
        retrofit.create(PlaceReviewService::class.java)

    @ActivityRetainedScoped
    @Provides
    fun providePlaceReviewDataSource(service: PlaceReviewService): PlaceReviewDataSource =
        PlaceReviewDataSource(service)

    @ActivityRetainedScoped
    @Provides
    fun providePlaceReviewRepository(dataSource: PlaceReviewDataSource): PlaceReviewRepository =
        PlaceReviewRepositoryImpl(dataSource)

    @ActivityRetainedScoped
    @Provides
    fun provideFavoritePlaceService(@AuthNetworkObject retrofit: Retrofit): FavoritePlaceService =
        retrofit.create(FavoritePlaceService::class.java)

    @ActivityRetainedScoped
    @Provides
    fun provideFavoritePlaceDataSource(service: FavoritePlaceService): FavoritePlaceDateSource =
        FavoritePlaceDateSource(service)

    @ActivityRetainedScoped
    @Provides
    fun provideFavoritePlaceRepository(dataSource: FavoritePlaceDateSource): FavoritePlaceRepository =
        FavoritePlaceRepositoryImpl(dataSource)


    @ActivityRetainedScoped
    @Provides
    fun providePermissionDataSource(): PermissionDataSource =
        PermissionDataSource()

    @ActivityRetainedScoped
    @Provides
    fun provideTopicService(@AuthNetworkObject retrofit: Retrofit): TopicService =
        retrofit.create(TopicService::class.java)

    @ActivityRetainedScoped
    @Provides
    fun provideTopicDataSource(service: TopicService): TopicDataSource =
        TopicDataSource(service)

    @ActivityRetainedScoped
    @Provides
    fun provideTopicRepository(dataSource: TopicDataSource): TopicRepository =
        TopicRepositoryImpl(dataSource)

    @ActivityRetainedScoped
    @Provides
    fun provideTermService(@AuthNetworkObject retrofit: Retrofit): TermService =
        retrofit.create(TermService::class.java)

    @ActivityRetainedScoped
    @Provides
    fun provideTermDataSource(service: TermService): TermDataSource =
        TermDataSource(service)

    @ActivityRetainedScoped
    @Provides
    fun provideTermRepository(dataSource: TermDataSource): TermRepository =
        TermRepositoryImpl(dataSource)

    @ActivityRetainedScoped
    @Provides
    fun provideCommentService(@AuthNetworkObject retrofit: Retrofit): CommentService =
        retrofit.create(CommentService::class.java)

    @ActivityRetainedScoped
    @Provides
    fun provideCommentDataSource(service: CommentService): CommentDataSource =
        CommentDataSource(service)

    @ActivityRetainedScoped
    @Provides
    fun provideCommentRepository(dataSource: CommentDataSource): CommentRepository =
        CommentRepositoryImpl(dataSource)

    @ActivityRetainedScoped
    @Provides
    fun provideCommunityService(@AuthNetworkObject retrofit: Retrofit): CommunityService =
        retrofit.create(CommunityService::class.java)

    @ActivityRetainedScoped
    @Provides
    fun provideCommunityDataSource(service: CommunityService): CommunityDataSource =
        CommunityDataSource(service)

    @ActivityRetainedScoped
    @Provides
    fun provideCommunityRepository(
        dataSource: CommunityDataSource,
        imageRepositoryImpl: ImageRepositoryImpl,
        imageDataSource: ImageDataSource,
        moshi: Moshi
    ): CommunityRepository =
        CommunityRepositoryImpl(dataSource, imageRepositoryImpl, imageDataSource,moshi)

    @ActivityRetainedScoped
    @Provides
    fun provideUserService(@AuthNetworkObject retrofit: Retrofit): UserService =
        retrofit.create(UserService::class.java)
    @ActivityRetainedScoped
    @Provides
    fun provideUserDataSource(
        @ApplicationContext context: Context,
        userService: UserService,
    ): UserDataSource = UserDataSource(context, userService)

    @ActivityRetainedScoped
    @Provides
    fun provideUserRepository(
        userDataSource: UserDataSource,
        imageDataSource: ImageDataSource,
        imageRepositoryImpl: ImageRepositoryImpl,
        userInfoDataSource: UserInfoDataSource,
    ): UserRepository =
        UserRepositoryImpl(userDataSource, imageDataSource, imageRepositoryImpl, userInfoDataSource)

    @ActivityRetainedScoped
    @Provides
    fun provideImageRepository(dataSource: ImageDataSource): ImageRepository =
        ImageRepositoryImpl(dataSource)

    @ActivityRetainedScoped
    @Provides
    fun provideImageDataSource(@ApplicationContext context: Context): ImageDataSource =
        ImageDataSource(context.contentResolver)
}
