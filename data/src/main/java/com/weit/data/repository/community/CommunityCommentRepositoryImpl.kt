package com.weit.data.repository.community

import com.weit.data.source.CommunityDataSource
import com.weit.data.util.exception
import com.weit.domain.model.community.comment.CommunityCommentContent
import com.weit.domain.model.community.comment.CommunityCommentDeleteInfo
import com.weit.domain.model.community.comment.CommunityCommentInfo
import com.weit.domain.model.community.comment.CommunityCommentRegistrationInfo
import com.weit.domain.model.community.comment.CommunityCommentUpdateInfo
import com.weit.domain.model.exception.InvalidPermissionException
import com.weit.domain.model.exception.InvalidTokenException
import com.weit.domain.model.exception.NoMoreItemException
import com.weit.domain.model.exception.UnKnownException
import com.weit.domain.model.exception.community.NotExistCommunityIdOrCommunityCommentsException
import com.weit.domain.repository.community.comment.CommunityCommentRepository
import okhttp3.internal.http.HTTP_FORBIDDEN
import okhttp3.internal.http.HTTP_NOT_FOUND
import okhttp3.internal.http.HTTP_UNAUTHORIZED
import retrofit2.HttpException
import retrofit2.Response
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

class CommunityCommentRepositoryImpl @Inject constructor(
    private val communityDataSource: CommunityDataSource,
) : CommunityCommentRepository {

    private val hasNextComment = AtomicBoolean(true)

    override suspend fun registerCommunityComment(communityCommentRegistrationInfo: CommunityCommentRegistrationInfo): Result<Unit> {
        val result = runCatching {
            communityDataSource.registerCommunityComment(communityCommentRegistrationInfo.communityId,communityCommentRegistrationInfo.content)
        }
        return if (result.isSuccess) {
            Result.success(result.getOrThrow())
        } else {
            Result.failure(handleRegisterAndGetCommentError(result.exception()))
        }
    }

    override suspend fun getCommunityComments(communityCommentInfo: CommunityCommentInfo): Result<List<CommunityCommentContent>> {
        if (hasNextComment.get().not()) {
            return Result.failure(NoMoreItemException())
        }
        val result = runCatching {
            communityDataSource.getCommunityComments(communityCommentInfo.communityId,communityCommentInfo.size,communityCommentInfo.lastId)
        }
        return if (result.isSuccess) {
            val communityComments = result.getOrThrow()
            hasNextComment.set(communityComments.hasNext)
            Result.success(communityComments.content)
        } else {
            Result.failure(handleRegisterAndGetCommentError(result.exception()))
        }
    }

    override suspend fun updateCommunityComment(communityCommentUpdateInfo: CommunityCommentUpdateInfo): Result<Unit> {
        val response = communityDataSource.updateCommunityComment(communityCommentUpdateInfo.communityId,communityCommentUpdateInfo.commentId,communityCommentUpdateInfo.content)
        return if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(handleUpdateAndDeleteCommentError(response))
        }
    }

    override suspend fun deleteCommunityComment(communityCommentDeleteInfo: CommunityCommentDeleteInfo): Result<Unit> {
        val response = communityDataSource.deleteCommunityComment(communityCommentDeleteInfo.communityId,communityCommentDeleteInfo.commentId)
        return if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(handleUpdateAndDeleteCommentError(response))
        }
    }

    private fun handleUpdateAndDeleteCommentError(response: Response<*>): Throwable {
        return handleCode(response.code())
    }

    private fun handleRegisterAndGetCommentError(t: Throwable): Throwable {
        return if (t is HttpException) {
            handleCode(t.code())
        } else {
            t
        }
    }

    private fun handleCode(code: Int): Throwable {
        return when (code) {
            HTTP_NOT_FOUND -> NotExistCommunityIdOrCommunityCommentsException()
            HTTP_UNAUTHORIZED -> InvalidTokenException()
            HTTP_FORBIDDEN -> InvalidPermissionException()
            else -> UnKnownException()
        }
    }

}
