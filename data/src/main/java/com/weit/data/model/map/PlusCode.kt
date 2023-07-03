package com.weit.data.model.map

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PlusCode(
    @field:Json(name = "global_code") val globalCode: String,
    @field:Json(name = "compound_code") val compoundCode: String?,
)
