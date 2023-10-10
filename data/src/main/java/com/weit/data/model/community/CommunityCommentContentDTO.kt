package com.weit.data.model.community

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.weit.data.model.follow.FollowUserContentDTO
import com.weit.data.util.StringToLocalDateTime
import com.weit.domain.model.community.CommunityContentImage
import com.weit.domain.model.community.CommunityMainContent
import com.weit.domain.model.community.comment.CommunityCommentContent
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Date

@JsonClass(generateAdapter = true)
data class CommunityCommentContentDTO(
    @field:Json(name = "communityCommentId") override val communityCommentId: Long,
    @field:Json(name = "content") override val content: String,
    @field:Json(name = "updatedAt") @StringToLocalDateTime override val updatedAt: LocalDateTime,
    @field:Json(name = "isWriter") override val isWriter: Boolean,
    @field:Json(name = "user")override val user: FollowUserContentDTO,
) : CommunityCommentContent