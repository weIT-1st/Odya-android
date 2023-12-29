package com.weit.data.model.community

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.weit.data.model.follow.FollowUserContentDTO
import com.weit.data.util.StringToLocalDateTime
import com.weit.domain.model.community.MyActivityCommentContent
import com.weit.domain.model.community.comment.CommentContent
import java.time.LocalDateTime

@JsonClass(generateAdapter = true)
data class MyActivityCommentContentDTO(
    @field:Json(name = "communityCommentId") override val communityCommentId: Long,
    @field:Json(name = "content") override val content: String,
    @field:Json(name = "updatedAt") @StringToLocalDateTime override val updatedAt: LocalDateTime,
    @field:Json(name = "user")override val user: FollowUserContentDTO,
) : MyActivityCommentContent
