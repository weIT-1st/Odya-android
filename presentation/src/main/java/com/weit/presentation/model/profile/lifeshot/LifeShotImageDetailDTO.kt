package com.weit.presentation.model.profile.lifeshot

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LifeShotImageDetailDTO(
    val imageId: Long,
    val imageUrl: String,
    val placeId: String?,
    val isLifeShot: Boolean,
    val placeName: String?,
    val journalId: Long?,
    val communityId: Long?,
) : Parcelable
