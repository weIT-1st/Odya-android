package com.weit.presentation.model.post.place

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SelectPlaceDTO(
    val placeId: String,
    val name: String,
    val address: String,
    val position: Int, // 여행일지의 어느 day에 넣어져야하는지 구분
) : Parcelable
