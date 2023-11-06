package com.weit.data.model.auth.term

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.weit.domain.model.auth.TermContentInfo

@JsonClass(generateAdapter = true)
data class TermContentDTO(
    @field:Json(name = "id") override val termId: Long,
    @field:Json(name = "content") override val content: String,
) : TermContentInfo
