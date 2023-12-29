package com.weit.presentation.model.post.place

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlacePredictionDTO(
    val placeId: String,
    val name: String,
    val address: String,
) : Parcelable
