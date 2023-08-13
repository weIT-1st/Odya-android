package com.weit.data.model.user

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.weit.domain.model.user.UserProfile

@JsonClass(generateAdapter = true)
data class UserProfileDTO(
    @field:Json(name = "profileUrl") override val url: String,
    @field:Json(name = "profileColor") override val color: UserProfileColorDTO?,
) : UserProfile
