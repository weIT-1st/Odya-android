package com.weit.data.repository.community

import com.weit.data.source.CommentDataSource
import com.weit.data.util.exception
import com.weit.domain.model.community.comment.CommentContent
import com.weit.domain.model.community.comment.CommentDeleteInfo
import com.weit.domain.model.community.comment.CommentInfo
import com.weit.domain.model.community.comment.CommentRegistrationInfo
import com.weit.domain.model.community.comment.CommentUpdateInfo
import com.weit.domain.model.exception.InvalidPermissionException
import com.weit.domain.model.exception.InvalidTokenException
import com.weit.domain.model.exception.NoMoreItemException
import com.weit.domain.model.exception.UnKnownException
import com.weit.domain.model.exception.community.NotExistCommunityIdOrCommunityCommentsException
import com.weit.domain.repository.community.comment.CommentRepository
import okhttp3.internal.http.HTTP_FORBIDDEN
import okhttp3.internal.http.HTTP_NOT_FOUND
import okhttp3.internal.http.HTTP_UNAUTHORIZED
import retrofit2.HttpException
import retrofit2.Response
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

class CommentRepositoryImpl @Inject constructor(
    private val commentDataSource: CommentDataSource,
) : CommentRepository {

    private val hasNextComment = AtomicBoolean(true)

    override suspend fun registerCommunityComment(commentRegistrationInfo: CommentRegistrationInfo): Result<Unit> {
        val result = runCatching {
            commentDataSource.registerCommunityComment(commentRegistrationInfo.communityId,commentRegistrationInfo.content)
        }
        return if (result.isSuccess) {
            Result.success(result.getOrThrow())
        } else {
            Result.failure(handleRegisterAndGetCommentError(result.exception()))
        }
    }

    override suspend fun getCommunityComments(commentInfo: CommentInfo): Result<List<CommentContent>> {

        if(commentInfo.lastId == null){
            hasNextComment.set(true)
        }
       
        if (hasNextComment.get().not()) {
            return Result.failure(NoMoreItemException())
        }

        val result = runCatching {
            commentDataSource.getCommunityComments(commentInfo.communityId,commentInfo.size,commentInfo.lastId)
        }
        return if (result.isSuccess) {
            val communityComments = result.getOrThrow()
            hasNextComment.set(communityComments.hasNext)
            Result.success(communityComments.content)
        } else {
            Result.failure(handleRegisterAndGetCommentError(result.exception()))
        }
    }

    override suspend fun updateCommunityComment(commentUpdateInfo: CommentUpdateInfo): Result<Unit> {
        val response = commentDataSource.updateCommunityComment(commentUpdateInfo.communityId,commentUpdateInfo.commentId,commentUpdateInfo.content)
        return if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(handleUpdateAndDeleteCommentError(response))
        }
    }

    override suspend fun deleteCommunityComment(commentDeleteInfo: CommentDeleteInfo): Result<Unit> {
        val response = commentDataSource.deleteCommunityComment(commentDeleteInfo.communityId,commentDeleteInfo.commentId)
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
