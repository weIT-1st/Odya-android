package com.weit.domain.repository.community.comment

import com.weit.domain.model.community.comment.CommentContent
import com.weit.domain.model.community.comment.CommentDeleteInfo
import com.weit.domain.model.community.comment.CommentInfo
import com.weit.domain.model.community.comment.CommentRegistrationInfo
import com.weit.domain.model.community.comment.CommentUpdateInfo

interface CommentRepository {
    suspend fun registerCommunityComment(commentRegistrationInfo: CommentRegistrationInfo): Result<Unit>
    suspend fun getCommunityComments(commentInfo: CommentInfo): Result<List<CommentContent>>
    suspend fun updateCommunityComment(commentUpdateInfo: CommentUpdateInfo): Result<Unit>
    suspend fun deleteCommunityComment(commentDeleteInfo: CommentDeleteInfo) : Result<Unit>
}
