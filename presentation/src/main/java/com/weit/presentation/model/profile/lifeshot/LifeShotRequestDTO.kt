package com.weit.presentation.model.profile.lifeshot

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LifeShotRequestDTO(
    val lastId: Long,
    val userId: Long,
) : Parcelable
