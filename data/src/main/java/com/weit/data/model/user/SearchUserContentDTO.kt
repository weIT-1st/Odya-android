package com.weit.data.model.user

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.weit.data.model.user.UserProfileDTO
import com.weit.domain.model.follow.FollowUserContent
import com.weit.domain.model.user.SearchUserContent

@JsonClass(generateAdapter = true)
data class SearchUserContentDTO(
    @field:Json(name = "userId") override val userId: Long,
    @field:Json(name = "nickname") override val nickname: String,
    @field:Json(name = "profile") override val profile: UserProfileDTO,
    @field:Json(name = "isFollowing") override val isFollowing: Boolean,
    ) : SearchUserContent
