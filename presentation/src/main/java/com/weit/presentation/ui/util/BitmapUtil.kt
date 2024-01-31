package com.weit.presentation.ui.util

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.LayoutInflater
import android.view.View
import android.view.View.MeasureSpec
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toDrawable
import com.bumptech.glide.Glide
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.weit.presentation.R
import com.weit.presentation.databinding.ItemOdyaPinBinding
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

fun getImageMarkerIconFromLayout(
    context: Context,
    imageUrl: String?,
): BitmapDescriptor {
    val binding =
        ItemOdyaPinBinding.inflate(LayoutInflater.from(context), null, false)
    Glide.with(binding.root.context)
        .load(imageUrl)
        .placeholder(R.layout.image_placeholder.toDrawable())
        .into(binding.ivItemOdyaPin)

    val bitmap = getBitmapFromView(binding.root)
    binding.root.removeAllViews()

    return BitmapDescriptorFactory.fromBitmap(bitmap)
}

private fun getBitmapFromView(
    view: View,
): Bitmap {
    view.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
    view.measure(
        MeasureSpec.makeMeasureSpec(
            0,
            View.MeasureSpec.UNSPECIFIED
        ),
        MeasureSpec.makeMeasureSpec(
            0, MeasureSpec.UNSPECIFIED)
    )
    view.layout(0, 0, view.measuredWidth, view.measuredHeight)

    val bitmap = Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    view.draw(canvas)

    return bitmap
}

