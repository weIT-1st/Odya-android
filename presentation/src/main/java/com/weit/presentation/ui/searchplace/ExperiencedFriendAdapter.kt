package com.weit.presentation.ui.searchplace

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.weit.domain.model.follow.ExperiencedFriendContent
import com.weit.presentation.databinding.ItemRoundProfileBigBinding

class ExperiencedFriendAdapter() : ListAdapter<ExperiencedFriendContent, ExperiencedFriendAdapter.ViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExperiencedFriendAdapter.ViewHolder {
        return ViewHolder(ItemRoundProfileBigBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ExperiencedFriendAdapter.ViewHolder, position: Int) {
    }

    inner class ViewHolder(
        private val binding: ItemRoundProfileBigBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ExperiencedFriendContent) {
            binding.profile = item
        }
    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<ExperiencedFriendContent>() {
            override fun areItemsTheSame(
                oldItem: ExperiencedFriendContent,
                newItem: ExperiencedFriendContent,
            ): Boolean = oldItem.userId == newItem.userId

            override fun areContentsTheSame(
                oldItem: ExperiencedFriendContent,
                newItem: ExperiencedFriendContent,
            ): Boolean = oldItem == newItem
        }
    }
}
