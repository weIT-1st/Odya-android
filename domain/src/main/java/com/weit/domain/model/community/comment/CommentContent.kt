package com.weit.domain.model.community.comment


import com.weit.domain.model.follow.FollowUserContent
import java.time.LocalDateTime

interface CommentContent {
    val communityCommentId: Long
    val content: String
    val updatedAt: LocalDateTime
    val isWriter: Boolean
    val user: FollowUserContent
}
