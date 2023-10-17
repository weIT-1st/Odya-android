package com.weit.presentation.model.feed

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FeedImage(
    val images: List<String> = emptyList(),
): Parcelable