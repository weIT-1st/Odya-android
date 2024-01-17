package com.weit.presentation.model.profile.lifeshot

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SelectLifeShotPlaceDTO(
    val placeId: String,
    val name: String,
    val address: String,
) : Parcelable
