package com.weit.data.model.bookmark

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.weit.data.model.user.UserProfileDTO
import com.weit.domain.model.community.CommunityUser

@JsonClass(generateAdapter = true)
data class WriterDTO(
    @field:Json(name = "userId") val userId: Long,
    @field:Json(name = "nickname") val nickname: String,
    @field:Json(name = "profile") val profile: UserProfileDTO,
    @field:Json(name = "isFollowing") val isFollowing: Boolean,
)
