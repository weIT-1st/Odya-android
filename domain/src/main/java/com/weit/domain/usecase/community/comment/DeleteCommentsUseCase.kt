package com.weit.domain.usecase.community.comment

import com.weit.domain.model.community.comment.CommentDeleteInfo
import com.weit.domain.repository.community.comment.CommentRepository
import javax.inject.Inject

class DeleteCommentsUseCase @Inject constructor(
    private val commentRepository: CommentRepository,
) {
    suspend operator fun invoke(commentDeleteInfo: CommentDeleteInfo): Result<Unit> =
        commentRepository.deleteCommunityComment(commentDeleteInfo)
}
