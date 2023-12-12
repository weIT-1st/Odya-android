package com.weit.presentation.ui.util

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.weit.presentation.R
import kotlin.math.roundToInt

fun getMarkerIconFromDrawable(
    resources: Resources,
    @DrawableRes drawableRes: Int = R.drawable.ic_marker,
    @DimenRes widthDp: Int = R.dimen.marker_width,
    @DimenRes heightDp: Int = R.dimen.marker_height,
): BitmapDescriptor? {
    val width = resources.getDimension(widthDp).roundToInt()
    val height = resources.getDimension(heightDp).roundToInt()
    val drawable = ResourcesCompat.getDrawable(resources, drawableRes, null) ?: return null
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas().apply {
        setBitmap(bitmap)
    }
    drawable.run {
        setBounds(0, 0, width, height)
        draw(canvas)
    }
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}
