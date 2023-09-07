package com.weit.presentation.ui.searchplace

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.weit.domain.model.follow.ExperiencedFriendInfo
import com.weit.domain.model.place.PlaceReviewInfo
import com.weit.presentation.databinding.ItemRoundProfile32dpBinding

class ExperiencedFriendAdapter(

) : ListAdapter<ExperiencedFriendInfo, RecyclerView.ViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(ItemRoundProfile32dpBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemRoundProfile32dpBinding
    ): RecyclerView.ViewHolder(binding.root){
        fun bind(item: ExperiencedFriendInfo){
            binding.profile = item
        }
    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<ExperiencedFriendInfo>() {
            override fun areItemsTheSame(
                oldItem: ExperiencedFriendInfo,
                newItem: ExperiencedFriendInfo,
            ): Boolean = oldItem.userId == newItem.userId

            override fun areContentsTheSame(
                oldItem: ExperiencedFriendInfo,
                newItem: ExperiencedFriendInfo,
            ): Boolean = oldItem.toString() == newItem.toString()
        }
    }
}
