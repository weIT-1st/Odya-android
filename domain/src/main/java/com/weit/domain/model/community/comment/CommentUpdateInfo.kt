package com.weit.domain.model.community.comment

data class CommentUpdateInfo(
    val communityId: Long,
    val commentId: Long,
    val content: String,
)