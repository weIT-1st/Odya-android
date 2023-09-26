package com.weit.data.model.auth.term

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.weit.domain.model.auth.TermInfo

@JsonClass(generateAdapter = true)
data class TermDTO(
    @field:Json(name = "id") override val termId: Long,
    @field:Json(name = "title") override val title: String,
    @field:Json(name = "required") override val required: Boolean,
) : TermInfo
