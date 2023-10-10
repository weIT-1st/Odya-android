package com.weit.domain.usecase.community.comment

import com.weit.domain.model.community.comment.CommunityCommentContent
import com.weit.domain.model.community.comment.CommunityCommentInfo
import com.weit.domain.model.community.comment.CommunityCommentRegistrationInfo
import com.weit.domain.model.follow.FollowNumDetail
import com.weit.domain.model.follow.FollowUserIdInfo
import com.weit.domain.repository.community.comment.CommunityCommentRepository
import com.weit.domain.repository.follow.FollowRepository
import javax.inject.Inject

class RegisterCommunityCommentsUseCase @Inject constructor(
    private val communityCommentRepository: CommunityCommentRepository,
) {
    suspend operator fun invoke(communityCommentRegistrationInfo: CommunityCommentRegistrationInfo): Result<Unit> =
        communityCommentRepository.registerCommunityComment(communityCommentRegistrationInfo)
}
