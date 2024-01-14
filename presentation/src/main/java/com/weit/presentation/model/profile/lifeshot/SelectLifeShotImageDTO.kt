package com.weit.presentation.model.profile.lifeshot

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SelectLifeShotImageDTO(
    val imageId: Long,
    val imageUri: String,
) : Parcelable
