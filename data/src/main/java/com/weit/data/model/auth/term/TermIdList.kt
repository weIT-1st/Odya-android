package com.weit.data.model.auth.term

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TermIdList(
    @field:Json(name = "agreedTermsIdList") val agreedTermsIdList: List<Long>,
    @field:Json(name = "disagreeTermsIdList") val disagreeTermsIdList: List<Long>,
)
