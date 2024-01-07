package com.weit.presentation.ui.searchplace.journey

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue

object DimensionUtils {

    fun dpToPx(context: Context, dp: Int): Int {
        val resources: Resources = context.resources
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), resources.displayMetrics).toInt()
    }
}
