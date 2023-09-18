package com.weit.domain.repository.community.comment

import com.weit.domain.model.community.comment.CommunityCommentContent
import com.weit.domain.model.community.comment.CommunityCommentDeleteInfo
import com.weit.domain.model.community.comment.CommunityCommentInfo
import com.weit.domain.model.community.comment.CommunityCommentRegistrationInfo
import com.weit.domain.model.community.comment.CommunityCommentUpdateInfo

interface CommunityCommentRepository {
    suspend fun registerCommunityComment(communityCommentRegistrationInfo: CommunityCommentRegistrationInfo): Result<Unit>
    suspend fun getCommunityComments(communityCommentInfo: CommunityCommentInfo): Result<List<CommunityCommentContent>>
    suspend fun updateCommunityComment(communityCommentUpdateInfo: CommunityCommentUpdateInfo): Result<Unit>
    suspend fun deleteCommunityComment(communityCommentDeleteInfo: CommunityCommentDeleteInfo) : Result<Unit>
}
