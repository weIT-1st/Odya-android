package com.weit.presentation.ui.searchplace.review

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.weit.domain.model.place.PlaceReviewDetail
import com.weit.domain.model.place.PlaceReviewInfo
import com.weit.presentation.databinding.ItemMyPlaceReviewBinding
import com.weit.presentation.databinding.ItemPlaceReviewBinding

class PlaceReviewAdapter(
    private val myId: Long?
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
            is ReviewViewHolder -> holder.bind(item)
            is MyReviewViewHolder -> holder.bind(item)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).userId == myId){
            myReviewType
        } else {
            friendReviewType
        }
    }

    inner class ReviewViewHolder(
        private val binding: ItemPlaceReviewBinding
    ): RecyclerView.ViewHolder(binding.root){
        fun bind(item: PlaceReviewInfo){
            binding.tvItemPlaceReviewWriter.text = item.writerNickname
            binding.tvItemPlaceReviewContent.text = item.review
            binding.tvItemPlaceReviewDate.text = item.createAt
            binding.ratingbarItemPlaceReview.rating = item.rating
            binding.ivItemPlaceProfile
            binding.review = item
        }
    }

    inner class MyReviewViewHolder(
        private val binding: ItemMyPlaceReviewBinding
    ): RecyclerView.ViewHolder(binding.root){
        fun bind(item: PlaceReviewInfo){
            binding.tvItemPlaceReviewWriter.text = item.writerNickname
            binding.tvItemPlaceReviewContent.text = item.review
            binding.tvItemPlaceReviewDate.text = item.createAt
            binding.ratingbarItemPlaceReview.rating = item.rating / 2
            binding.ivItemPlaceProfile
            binding.review = item
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
