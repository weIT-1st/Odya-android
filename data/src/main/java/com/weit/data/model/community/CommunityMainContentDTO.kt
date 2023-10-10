package com.weit.data.model.community

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.weit.data.model.follow.FollowUserContentDTO
import com.weit.domain.model.community.CommunityContentImage
import com.weit.domain.model.community.CommunityMainContent
import com.weit.domain.model.community.comment.CommunityCommentContent
import java.time.LocalDate

@JsonClass(generateAdapter = true)
data class CommunityMainContentDTO(
    @field:Json(name = "communityId") override val communityId: Long,
    @field:Json(name = "placeId") override val placeId: String?,
    @field:Json(name = "communityMainImageUrl") override val communityMainImageUrl: String,
    @field:Json(name = "communityCommentCount") override val communityCommentCount: Int,
    @field:Json(name = "communityLikeCount") override val communityLikeCount: Int,
) : CommunityMainContent
