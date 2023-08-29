package com.weit.data.repository.topic

import com.weit.data.model.topic.TopicRegistration
import com.weit.data.source.TopicDataSource
import com.weit.data.util.exception
import com.weit.domain.model.exception.InvalidRequestException
import com.weit.domain.model.exception.InvalidTokenException
import com.weit.domain.model.exception.UnKnownException
import com.weit.domain.model.exception.topic.NotExistTopicIdException
import com.weit.domain.model.exception.topic.NotHavePermissionException
import com.weit.domain.model.topic.TopicDetail
import com.weit.domain.repository.topic.TopicRepository
import okhttp3.internal.http.HTTP_BAD_REQUEST
import okhttp3.internal.http.HTTP_FORBIDDEN
import okhttp3.internal.http.HTTP_NOT_FOUND
import okhttp3.internal.http.HTTP_UNAUTHORIZED
import retrofit2.HttpException
import retrofit2.Response
import javax.inject.Inject

class TopicRepositoryImpl @Inject constructor(
    private val dataSource: TopicDataSource,
) : TopicRepository {

    override suspend fun registerFavoriteTopic(topicIdList: List<Long>): Result<Unit> {
        return handleTopicResult {
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
        return handleTopicResult {
            dataSource.getTopicList()
        }
    }

    override suspend fun getFavoriteTopicList(): Result<List<TopicDetail>> {
        return handleTopicResult {
            dataSource.getFavoriteTopicList()
        }
    }

    private fun handleDeleteTopicError(response: Response<*>): Throwable {
        return handleCode(response.code())
    }
    private fun handleTopicError(t: Throwable): Throwable {
        return if (t is HttpException) {
            handleCode(t.code())
        } else {
            t
        }
    }

    private fun handleCode(code: Int): Throwable {
        return when (code) {
            HTTP_BAD_REQUEST -> InvalidRequestException()
            HTTP_NOT_FOUND -> NotExistTopicIdException()
            HTTP_UNAUTHORIZED -> InvalidTokenException()
            HTTP_FORBIDDEN -> NotHavePermissionException()
            else -> UnKnownException()
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
                Result.failure(handleTopicError(result.exception()))
            }
        } catch (t: Throwable) {
            Result.failure(handleTopicError(t))
        }
    }
}
