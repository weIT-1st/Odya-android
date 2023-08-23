package com.weit.presentation.ui.util

import android.content.res.Resources
import android.graphics.Rect
import android.view.View
import androidx.annotation.DimenRes
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.roundToInt

class SpaceDecoration(
    private val resources: Resources,
    @DimenRes private val rightDP: Int? = null,
    @DimenRes private val leftDP: Int? = null,
    @DimenRes private val bottomDP: Int? = null,
) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State,
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        rightDP?.let {
            outRect.right = getSpace(it)
        }
        leftDP?.let {
            outRect.left = getSpace(it)
        }
        bottomDP?.let {
            outRect.bottom = getSpace(it)
        }
    }

    private fun getSpace(@DimenRes dimenRes: Int) =
        resources.getDimension(dimenRes).roundToInt()
}
