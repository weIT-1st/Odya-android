package com.weit.presentation.ui.searchplace.review

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.weit.domain.model.place.PlaceReviewDetail
import com.weit.domain.model.place.PlaceReviewInfo
import com.weit.presentation.R
import com.weit.presentation.databinding.ItemMyPlaceReviewBinding
import com.weit.presentation.databinding.ItemPlaceReviewBinding
import kotlin.coroutines.coroutineContext

class PlaceReviewAdapter(
    private val context: Context?,
    private val updateItem: () -> Unit,
    private val deleteItem: () -> Unit
): ListAdapter<PlaceReviewInfo, RecyclerView.ViewHolder>(diffUtil) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            friendReviewType -> ReviewViewHolder(ItemPlaceReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            myReviewType -> MyReviewViewHolder(ItemMyPlaceReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            else -> throw IllegalArgumentException("Unknown view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when(holder){
            is ReviewViewHolder -> {
                holder.bind(item)
            }
            is MyReviewViewHolder -> {
                holder.bind(item)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).isMine){
            myReviewType
        } else {
            friendReviewType
        }
    }

    inner class ReviewViewHolder(
        private val binding: ItemPlaceReviewBinding,
    ): RecyclerView.ViewHolder(binding.root){
        fun bind(item: PlaceReviewInfo){
            binding.review = item
            binding.btnItemPlaceMenu.setOnClickListener { it ->
                PopupMenu(context, it).apply{
                    menuInflater.inflate(R.menu.friend_place_review, this.menu)

                    setOnMenuItemClickListener {
                        when(it.itemId){
                            R.id.item_report_review -> {}
                        }
                        false
                    }
                }.show()
            }
        }
    }

    inner class MyReviewViewHolder(
        private val binding: ItemMyPlaceReviewBinding,
    ): RecyclerView.ViewHolder(binding.root){
        fun bind(item: PlaceReviewInfo){
            binding.review = item
            binding.btnItemPlaceMenu.setOnClickListener { it ->
                PopupMenu(context, it).apply{
                    menuInflater.inflate(R.menu.my_place_reivew_menu, this.menu)

                    setOnMenuItemClickListener {
                        when(it.itemId){
                            R.id.item_update_review -> {
                                 updateItem()
                            }
                            R.id.item_delete_review -> {
                                deleteItem()
                            }
                        }
                        false
                    }
                }.show()
            }
        }
    }

    companion object {
        private const val friendReviewType = 1
        private const val myReviewType = 2

        private val diffUtil = object : DiffUtil.ItemCallback<PlaceReviewInfo>() {
            override fun areItemsTheSame(
                oldItem: PlaceReviewInfo,
                newItem: PlaceReviewInfo,
            ): Boolean = oldItem.userId == newItem.userId

            override fun areContentsTheSame(
                oldItem: PlaceReviewInfo,
                newItem: PlaceReviewInfo,
            ): Boolean = oldItem.toString() == newItem.toString()
        }
    }
}
