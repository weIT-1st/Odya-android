package com.weit.domain.usecase.community.comment

import com.weit.domain.model.community.comment.CommentRegistrationInfo
import com.weit.domain.repository.community.comment.CommentRepository
import javax.inject.Inject

class RegisterCommentsUseCase @Inject constructor(
    private val commentRepository: CommentRepository,
) {
    suspend operator fun invoke(commentRegistrationInfo: CommentRegistrationInfo): Result<Unit> =
        commentRepository.registerCommunityComment(commentRegistrationInfo)
}
