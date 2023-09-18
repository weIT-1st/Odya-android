package com.weit.domain.model.community.comment

data class CommunityCommentUpdateInfo(
    val communityId: Long,
    val commentId: Long,
    val content: String,
)