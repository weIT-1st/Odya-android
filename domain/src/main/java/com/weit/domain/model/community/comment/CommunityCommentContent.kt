package com.weit.domain.model.community.comment


import com.weit.domain.model.follow.FollowUserContent
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Date

interface CommunityCommentContent {
    val communityCommentId: Long
    val content: String
    val updatedAt: LocalDateTime
    val isWriter: Boolean
    val user: FollowUserContent
}
