package com.weit.data.model.community

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.weit.data.model.user.UserProfileDTO
import com.weit.domain.model.community.comment.CommunityCommentContent
import com.weit.domain.model.follow.FollowUserContent
import java.time.LocalDate

@JsonClass(generateAdapter = true)
data class CommunityContentDTO(
    @field:Json(name = "communityCommentId") override val communityCommentId: Long,
    @field:Json(name = "content") override val content: String,
    @field:Json(name = "updatedAt") override val updatedAt: LocalDate,
    @field:Json(name = "isWriter") override val isWriter: Boolean,
    @field:Json(name = "user")override val user: FollowUserContent,
) : CommunityCommentContent