package com.weit.presentation.ui.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.weit.presentation.model.MayKnowFriend
import com.weit.presentation.databinding.ItemMayknowFriendSummaryBinding

class MayKnowFriendAdapter(
) : ListAdapter<MayKnowFriend, MayKnowFriendAdapter.MayKnowFriendViewHolder>(MayKnowFriendCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MayKnowFriendViewHolder {
        return MayKnowFriendViewHolder(
            ItemMayknowFriendSummaryBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        )
    }

    override fun onBindViewHolder(holder: MayKnowFriendViewHolder, position: Int) {
        val friend = getItem(position)
        holder.bind(friend)
    }

    class MayKnowFriendViewHolder(
        private val binding: ItemMayknowFriendSummaryBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(mayKnowFriend: MayKnowFriend) {
            binding.tvMayknowFriendSummaryNickname.text = mayKnowFriend.nickname
            binding.tvMayknowFriendSummaryFeature.text = mayKnowFriend.feature
        }
    }
    companion object {
        private val MayKnowFriendCallback: DiffUtil.ItemCallback<MayKnowFriend> =
            object : DiffUtil.ItemCallback<MayKnowFriend>() {
                override fun areItemsTheSame(oldItem: MayKnowFriend, newItem: MayKnowFriend): Boolean {
                    return oldItem.nickname == newItem.nickname
                }

                override fun areContentsTheSame(oldItem: MayKnowFriend, newItem: MayKnowFriend): Boolean {
                    return oldItem == newItem
                }
            }
    }
}
