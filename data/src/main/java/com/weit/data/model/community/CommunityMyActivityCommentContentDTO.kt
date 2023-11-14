package com.weit.data.model.community

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.weit.domain.model.community.CommunityMainContent
import com.weit.domain.model.community.CommunityMyActivityCommentContent
import com.weit.domain.model.community.CommunityMyActivityContent
import com.weit.domain.model.community.CommunityUser
import com.weit.domain.model.community.MyActivityCommentContent

@JsonClass(generateAdapter = true)
data class CommunityMyActivityCommentContentDTO(
    @field:Json(name = "communityId") override val communityId: Long,
    @field:Json(name = "communityMainImageUrl") override val communityMainImageUrl: String,
    @field:Json(name = "communityContent") override val communityContent: String,
    @field:Json(name = "updatedAt") override val updatedAt: String,
    @field:Json(name = "writer") override val writer: CommunityUserDTO,
    @field:Json(name = "communityCommentSimpleResponse") override val communityCommentSimpleResponse: MyActivityCommentContentDTO,
    ) : CommunityMyActivityCommentContent
