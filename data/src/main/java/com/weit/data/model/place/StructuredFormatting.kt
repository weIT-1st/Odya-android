package com.weit.data.model.place

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StructuredFormatting(
    @field:Json(name = "main_text") val mainText: String,
    @field:Json(name = "main_text_matched_substrings") val mainTextMatchedSubStrings: List<MatchedSubString>,
    @field:Json(name = "secondary_text") val secondaryText: String?,
    @field:Json(name = "secondary_text_matched_substrings") val secondaryTextMatchedSubStrings: List<MatchedSubString>?,
)
