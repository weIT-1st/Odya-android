package com.weit.data.model.map

import com.google.android.gms.maps.model.LatLng
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class PlacePhoto(
    @field:Json(name = "height")val height: Int,
    @field:Json(name = "html_attributions")val htmlAttributions: Array<String>,
    @field:Json(name = "photo_reference")val photoReference: String,
    @field:Json(name = "width")val width: Int)
