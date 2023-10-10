package com.weit.domain.usecase.community.comment

import com.weit.domain.model.community.comment.CommunityCommentContent
import com.weit.domain.model.community.comment.CommunityCommentDeleteInfo
import com.weit.domain.model.community.comment.CommunityCommentInfo
import com.weit.domain.model.follow.FollowNumDetail
import com.weit.domain.model.follow.FollowUserIdInfo
import com.weit.domain.repository.community.comment.CommunityCommentRepository
import com.weit.domain.repository.follow.FollowRepository
import javax.inject.Inject

class DeleteCommunityCommentsUseCase @Inject constructor(
    private val communityCommentRepository: CommunityCommentRepository,
) {
    suspend operator fun invoke(communityCommentDeleteInfo: CommunityCommentDeleteInfo): Result<Unit> =
        communityCommentRepository.deleteCommunityComment(communityCommentDeleteInfo)
}
