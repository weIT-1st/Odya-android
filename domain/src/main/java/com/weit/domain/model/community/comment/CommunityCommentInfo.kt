package com.weit.domain.model.community.comment

data class CommunityCommentInfo(
    val communityId: Long,
    val size: Int? = null,
    val lastId:Long? = null,
)