package com.weit.presentation.ui.journal.memory.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.appcompat.view.ContextThemeWrapper
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.weit.domain.model.place.PlaceMyReviewInfo
import com.weit.presentation.R
import com.weit.presentation.databinding.ItemMyReviewBinding
import java.time.LocalDateTime

class MyReviewAdapter(
    private val deleteItem: (Long) -> Unit
): ListAdapter<PlaceMyReviewInfo, MyReviewAdapter.ViewHolder>(diffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(ItemMyReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class ViewHolder(
        private val binding: ItemMyReviewBinding
    ): RecyclerView.ViewHolder(binding.root){
        fun bind(item: PlaceMyReviewInfo) {
            binding.review = item
            binding.tvItemPlaceReviewDate.text = localDateTimeToString(item.createAt)
            binding.btnItemPlaceMenu.setOnClickListener {
                showPopUpMenu(binding.btnItemPlaceMenu, item.placeReviewId)
            }
        }
    }
    private fun showPopUpMenu(it: View, placeReviewId: Long){
        val contextThemeWrapper = ContextThemeWrapper(it.context, R.style.MemoryPopupMenuStyle)

        PopupMenu(contextThemeWrapper, it).apply {
            menuInflater.inflate(R.menu.menu_memory, this.menu)

            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.item_memory_delete -> {
                        deleteItem(placeReviewId)
                    }
                }
                false
            }
        }.show()
    }

    private fun localDateTimeToString(createAt: LocalDateTime): String =
        "${createAt.year}.${createAt.monthValue}.${createAt.dayOfMonth}"

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<PlaceMyReviewInfo>() {
            override fun areItemsTheSame(
                oldItem: PlaceMyReviewInfo,
                newItem: PlaceMyReviewInfo
            ): Boolean =
                oldItem.placeReviewId == newItem.placeReviewId

            override fun areContentsTheSame(
                oldItem: PlaceMyReviewInfo,
                newItem: PlaceMyReviewInfo
            ): Boolean =
                oldItem == newItem
        }
    }
}
