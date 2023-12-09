package com.weit.data.model.community

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.weit.domain.model.community.CommunityMainContent
import com.weit.domain.model.community.CommunityMyActivityContent

@JsonClass(generateAdapter = true)
data class CommunityMyActivityContentDTO(
    @field:Json(name = "communityId") override val communityId: Long,
    @field:Json(name = "communityMainImageUrl") override val communityMainImageUrl: String,
    @field:Json(name = "placeId") override val placeId: String?,
    ) : CommunityMyActivityContent
