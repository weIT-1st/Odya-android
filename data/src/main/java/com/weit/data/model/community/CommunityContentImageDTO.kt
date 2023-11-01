package com.weit.data.model.community

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.weit.domain.model.community.CommunityContentImage

@JsonClass(generateAdapter = true)
data class CommunityContentImageDTO(
    @field:Json(name = "communityContentImageId") override val communityContentImageId: Long,
    @field:Json(name = "imageUrl") override val imageUrl: String,
) : CommunityContentImage
