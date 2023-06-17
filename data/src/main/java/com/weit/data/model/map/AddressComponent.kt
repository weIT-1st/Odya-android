package com.weit.data.model.map

import com.google.android.gms.maps.model.LatLng
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class AddressComponent(
    @field:Json(name = "long_name")val longName: String,
    @field:Json(name = "short_name")val shortName: String,
    @field:Json(name = "types")val types: List<String>
)
