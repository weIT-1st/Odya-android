package com.weit.domain.model.community.comment


import com.weit.domain.model.follow.FollowUserContent
import java.time.LocalDate

interface CommunityCommentContent {
    val communityCommentId: Long
    val content: String
    val updatedAt: LocalDate
    val isWriter: Boolean
    val user: FollowUserContent
}
