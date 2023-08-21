package com.weit.data.repository.topic

import com.weit.data.model.favoritePlace.FavoritePlaceRegistration
import com.weit.data.model.topic.TopicRegistration
import com.weit.data.source.FavoritePlaceDateSource
import com.weit.data.source.TopicDataSource
import com.weit.domain.model.exception.InvalidRequestException
import com.weit.domain.model.exception.InvalidTokenException
import com.weit.domain.model.exception.UnKnownException
import com.weit.domain.model.exception.favoritePlace.NotExistPlaceIdException
import com.weit.domain.model.exception.favoritePlace.RegisteredFavoritePlaceException
import com.weit.domain.model.exception.topic.NotExistTopicIdException
import com.weit.domain.model.exception.topic.NotHavePermissionException
import com.weit.domain.model.favoritePlace.FavoritePlaceDetail
import com.weit.domain.model.favoritePlace.FavoritePlaceInfo
import com.weit.domain.model.topic.TopicDetail
import com.weit.domain.repository.favoritePlace.FavoritePlaceRepository
import com.weit.domain.repository.topic.TopicRepository
import okhttp3.internal.http.HTTP_BAD_REQUEST
import okhttp3.internal.http.HTTP_CONFLICT
import okhttp3.internal.http.HTTP_FORBIDDEN
import okhttp3.internal.http.HTTP_INTERNAL_SERVER_ERROR
import okhttp3.internal.http.HTTP_NOT_FOUND
import okhttp3.internal.http.HTTP_UNAUTHORIZED
import retrofit2.HttpException
import retrofit2.Response
import javax.inject.Inject

class TopicRepositoryImpl @Inject constructor(
    private val dataSource: TopicDataSource,
) : TopicRepository {

    override suspend fun registerFavoriteTopic(topicIdList: List<Long>): Result<Unit> {
        return handleTopicResult{
            dataSource.registerFavoriteTopic(TopicRegistration(topicIdList))
        }
    }

    override suspend fun deleteFavoriteTopic(topicId: Long): Result<Unit> {
        val result = dataSource.deleteFavoriteTopic(topicId)
        return if (result.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(handleDeleteTopicError(result))
        }
    }

    override suspend fun getTopicList(): Result<List<TopicDetail>> {
        return handleTopicResult{
            dataSource.getTopicList().map {
                TopicDetail(it.topicWord)
            }
        }
    }

    override suspend fun getFavoriteTopicList(): Result<List<TopicDetail>> {
        return handleTopicResult{
            dataSource.getFavoriteTopicList().map {
                TopicDetail(it.topicWord)
            }
        }
    }

    //이렇게 함수를 두개 만들어야 하는것인가..
    private fun handleDeleteTopicError(response: Response<*>): Throwable {
        return when (response.code()) {
            HTTP_BAD_REQUEST -> InvalidRequestException()
            HTTP_NOT_FOUND -> NotExistTopicIdException()
            HTTP_UNAUTHORIZED -> InvalidTokenException()
            HTTP_FORBIDDEN -> NotHavePermissionException()
            else -> UnKnownException()
        }
    }
    private fun handleTopicError(t: Throwable): Throwable {
        return if (t is HttpException) {
            when (t.code()) {
                HTTP_BAD_REQUEST -> InvalidRequestException()
                HTTP_UNAUTHORIZED -> InvalidTokenException()
                HTTP_NOT_FOUND -> NotExistTopicIdException()
                else -> UnKnownException()
            }
        } else {
            t
        }
    }

    private inline fun <T> handleTopicResult(
        block: () -> T,
    ): Result<T> {
        return try {
            val result = runCatching(block)
            if (result.isSuccess) {
                Result.success(result.getOrThrow())
            } else {
                Result.failure(handleTopicError(result.exceptionOrNull()!!))
            }
        } catch (t: Throwable) {
            Result.failure(handleTopicError(t))
        }
    }
}
