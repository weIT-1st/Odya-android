package com.weit.data.model.map

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Place(
    @field:Json(name = "address_components")val addressComponents: Array<AddressComponent>?,
    @field:Json(name = "adr_address")val adrAddress: String?,
    @field:Json(name = "business_status")val businessStatus: String?,
    @field:Json(name = "curbside_pickup")val curbsidePickup: Boolean?,
    @field:Json(name = "delivery")val delivery: Boolean?,
    @field:Json(name = "dine_in")val dineIn: Boolean?,
    @field:Json(name = "editorial_summary")val editorialSummary: PlaceEditorialSummary?,
    @field:Json(name = "formatted_address")val formattedAddress: String?,
    @field:Json(name = "formatted_phone_number")val formattedPhoneNumber: String?,
    @field:Json(name = "geometry")val geometry: Geometry?,
    @field:Json(name = "icon")val icon: String?,
    @field:Json(name = "icon_background_color")val iconBackgroundColor: String?,
    @field:Json(name = "icon_mask_base_uri")val iconMaskBaseUri: String?,
    @field:Json(name = "international_phone_number")val internationalPhoneNumber: String?,
    @field:Json(name = "name")val name: String?,
    @field:Json(name = "photos")val photos: Array<PlacePhoto>?,
    @field:Json(name = "place_id")val placeId: String?,
    @field:Json(name = "plus_code")val plusCode: PlusCode?,
    @field:Json(name = "price_level")val priceLevel: Int?,
    @field:Json(name = "reservable")val reservable: Boolean?,
    @field:Json(name = "types")val types: Array<String>?,
    @field:Json(name = "url")val url: String?,
    @field:Json(name = "user_ratings_total")val userRatingsTotal: Int?,
    @field:Json(name = "utc_offset")val utcOffset: Int?,
    @field:Json(name = "vicinity")val vicinity: String?,
    @field:Json(name = "website")val website: String?,
)













