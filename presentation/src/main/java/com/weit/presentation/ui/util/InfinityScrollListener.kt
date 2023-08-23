package com.weit.presentation.ui.util

import androidx.recyclerview.widget.RecyclerView

abstract class InfinityScrollListener : RecyclerView.OnScrollListener() {

    abstract fun loadNextPage()

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        recyclerView.run {
            if (computeVerticalScrollRange() - computeVerticalScrollOffset() <= computeVerticalScrollExtent() * 2) {
                loadNextPage()
            }
        }
    }
}
