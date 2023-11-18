package com.weit.data.model.auth.term

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.weit.domain.model.auth.AgreedTermInfo

@JsonClass(generateAdapter = true)
data class AgreedTermDTO(
    @field:Json(name = "id") override val agreedTermId: Long,
    @field:Json(name = "userId")override val userId: Long,
    @field:Json(name = "termsId") override val termsId: Long,
    @field:Json(name = "required") override val required: Boolean,
) : AgreedTermInfo
