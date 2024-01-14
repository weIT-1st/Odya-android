package com.weit.presentation.model.feed

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SelectFeedPlaceDTO(
    val placeId: String,
    val name: String,
    val address: String,
) : Parcelable
