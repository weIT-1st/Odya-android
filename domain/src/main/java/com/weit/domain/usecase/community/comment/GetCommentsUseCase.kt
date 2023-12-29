package com.weit.domain.usecase.community.comment

import com.weit.domain.model.community.comment.CommentContent
import com.weit.domain.model.community.comment.CommentInfo
import com.weit.domain.repository.community.comment.CommentRepository
import javax.inject.Inject

class GetCommentsUseCase @Inject constructor(
    private val commentRepository: CommentRepository,
) {
    suspend operator fun invoke(commentInfo: CommentInfo): Result<List<CommentContent>> =
        commentRepository.getCommunityComments(commentInfo)
}
