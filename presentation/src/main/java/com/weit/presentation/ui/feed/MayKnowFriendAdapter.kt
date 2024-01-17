package com.weit.presentation.ui.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.weit.domain.model.follow.FollowUserContent
import com.weit.presentation.databinding.ItemMayknowFriendSummaryBinding

class MayKnowFriendAdapter(private val onFollowChanged: (Int, Long, Boolean) -> Unit) :
    ListAdapter<FollowUserContent, MayKnowFriendAdapter.MayKnowFriendViewHolder>(MayKnowFriendCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MayKnowFriendViewHolder {
        return MayKnowFriendViewHolder(
            ItemMayknowFriendSummaryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            ),
        )
    }

    override fun onBindViewHolder(holder: MayKnowFriendViewHolder, position: Int) {
        val friend = getItem(position)
        holder.bind(friend)
    }

    inner class MayKnowFriendViewHolder(
        private val binding: ItemMayknowFriendSummaryBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        init{
            binding.btnMayknowFriendSummaryFollow.setOnClickListener {
                val item = getItem(absoluteAdapterPosition)
                onFollowChanged(absoluteAdapterPosition, item.userId, false)
            }
        }
        fun bind(mayKnowFriend: FollowUserContent) {
            binding.friend = mayKnowFriend
        }
    }

    companion object {
        private val MayKnowFriendCallback: DiffUtil.ItemCallback<FollowUserContent> =
            object : DiffUtil.ItemCallback<FollowUserContent>() {
                override fun areItemsTheSame(
                    oldItem: FollowUserContent,
                    newItem: FollowUserContent,
                ): Boolean {
                    return oldItem.userId == newItem.userId
                }

                override fun areContentsTheSame(
                    oldItem: FollowUserContent,
                    newItem: FollowUserContent,
                ): Boolean {
                    return oldItem.toString() == newItem.toString()
                }
            }
    }
}
