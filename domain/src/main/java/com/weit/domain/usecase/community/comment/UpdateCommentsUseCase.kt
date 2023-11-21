package com.weit.domain.usecase.community.comment

import com.weit.domain.model.community.comment.CommentUpdateInfo
import com.weit.domain.repository.community.comment.CommentRepository
import javax.inject.Inject

class UpdateCommentsUseCase @Inject constructor(
    private val commentRepository: CommentRepository,
) {
    suspend operator fun invoke(commentUpdateInfo: CommentUpdateInfo): Result<Unit> =
        commentRepository.updateCommunityComment(commentUpdateInfo)
}
