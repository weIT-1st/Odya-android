package com.weit.data.model.auth

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class IsDuplicateDTO (
    @field:Json(name = "code") val code: Int,
    @field:Json(name = "errroMessage") val errorMesssage: String
        )
