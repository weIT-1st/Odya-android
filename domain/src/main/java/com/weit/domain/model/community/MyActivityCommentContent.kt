package com.weit.domain.model.community


import com.weit.domain.model.follow.FollowUserContent
import java.time.LocalDateTime

interface MyActivityCommentContent {
    val communityCommentId: Long
    val content: String
    val updatedAt: LocalDateTime
    val user: FollowUserContent
}
