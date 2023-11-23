package com.weit.presentation.ui.searchplace.review

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.weit.domain.model.place.PlaceReviewInfo
import com.weit.presentation.R
import com.weit.presentation.databinding.ItemMyPlaceReviewBinding
import com.weit.presentation.databinding.ItemPlaceReviewBinding
import java.time.LocalDateTime

class PlaceReviewAdapter(
    private val updateItem: () -> Unit,
    private val deleteItem: () -> Unit,
    private val reportItem: (placeReviewId: Long) -> Unit,
) : ListAdapter<PlaceReviewInfo, RecyclerView.ViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            friendReviewType -> ReviewViewHolder(ItemPlaceReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            myReviewType -> MyReviewViewHolder(ItemMyPlaceReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            else -> throw IllegalArgumentException("Unknown view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is ReviewViewHolder -> {
                holder.bind(item)
            }
            is MyReviewViewHolder -> {
                holder.bind(item)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).isMine) {
            myReviewType
        } else {
            friendReviewType
        }
    }

    inner class ReviewViewHolder(
        private val binding: ItemPlaceReviewBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PlaceReviewInfo) {
            binding.review = item
            binding.tvItemPlaceReviewDate.text = localDateTimeToString(item.createAt)
            showPopUpMenu(friendReviewType, binding.root.rootView, item.placeReviewId)
        }
    }

    inner class MyReviewViewHolder(
        private val binding: ItemMyPlaceReviewBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PlaceReviewInfo) {
            binding.review = item
            binding.tvItemPlaceReviewDate.text = localDateTimeToString(item.createAt)
            showPopUpMenu(myReviewType, binding.root.rootView, item.placeReviewId)
        }
    }

    private fun showPopUpMenu(type: Int, it: View, placeReviewId: Long) {
        PopupMenu(it.context, it).apply {
            when (type) {
                friendReviewType -> menuInflater.inflate(R.menu.friend_place_review, this.menu)
                myReviewType -> menuInflater.inflate(R.menu.my_place_reivew_menu, this.menu)
            }

            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.item_update_review -> {
                        updateItem()
                    }
                    R.id.item_delete_review -> {
                        deleteItem()
                    }
                    R.id.item_report_review -> {
                        reportItem(placeReviewId)
                    }
                }
                false
            }
        }.show()
    }

    private fun localDateTimeToString(createAt: LocalDateTime): String =
        "${createAt.year}.${createAt.monthValue}.${createAt.dayOfMonth}"

    companion object {
        private const val friendReviewType = 1
        private const val myReviewType = 2

        private val diffUtil = object : DiffUtil.ItemCallback<PlaceReviewInfo>() {
            override fun areItemsTheSame(
                oldItem: PlaceReviewInfo,
                newItem: PlaceReviewInfo,
            ): Boolean = oldItem.placeReviewId == newItem.placeReviewId

            override fun areContentsTheSame(
                oldItem: PlaceReviewInfo,
                newItem: PlaceReviewInfo,
            ): Boolean = oldItem == newItem
        }
    }
}
