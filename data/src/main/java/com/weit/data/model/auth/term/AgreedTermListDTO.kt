package com.weit.data.model.auth.term

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AgreedTermListDTO(
    @field:Json(name = "optionalAgreedTermsList") val optionalAgreedTermsList: List<TermDTO>,
    @field:Json(name = "userOptionalAgreedTermsList") val userOptionalAgreedTermsList: List<AgreedTermDTO>,
)
