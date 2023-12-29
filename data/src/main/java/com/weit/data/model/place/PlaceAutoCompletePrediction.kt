package com.weit.data.model.place

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PlaceAutoCompletePrediction(
    @field:Json(name = "description") val description: String,
    @field:Json(name = "matched_substrings") val matchedSubStrings: List<MatchedSubString>,
    @field:Json(name = "structured_formatting") val structuredFormatting: StructuredFormatting,
    @field:Json(name = "terms") val terms: List<PlaceAutoCompleteTerm>,
    @field:Json(name = "distance_meters") val distanceMeters: Int?,
    @field:Json(name = "place_id") val placeId: String,
    @field:Json(name = "types") val types: List<String>?,
)
