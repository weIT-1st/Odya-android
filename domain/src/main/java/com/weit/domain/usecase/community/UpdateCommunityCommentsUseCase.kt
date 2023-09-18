package com.weit.domain.usecase.community

import com.weit.domain.model.community.comment.CommunityCommentContent
import com.weit.domain.model.community.comment.CommunityCommentInfo
import com.weit.domain.model.community.comment.CommunityCommentUpdateInfo
import com.weit.domain.model.follow.FollowNumDetail
import com.weit.domain.model.follow.FollowUserIdInfo
import com.weit.domain.repository.community.comment.CommunityCommentRepository
import com.weit.domain.repository.follow.FollowRepository
import javax.inject.Inject

class UpdateCommunityCommentsUseCase @Inject constructor(
    private val communityCommentRepository: CommunityCommentRepository,
) {
    suspend operator fun invoke(communityCommentUpdateInfo: CommunityCommentUpdateInfo): Result<Unit> =
        communityCommentRepository.updateCommunityComment(communityCommentUpdateInfo)
}
