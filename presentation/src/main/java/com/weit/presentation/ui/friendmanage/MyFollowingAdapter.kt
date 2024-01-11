package com.weit.presentation.ui.friendmanage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.weit.domain.model.follow.FollowUserContent
import com.weit.presentation.databinding.ItemFriendManageFollowBinding
import com.weit.presentation.databinding.ItemTravelFriendSearchBinding

class MyFollowingAdapter(
    private val onClickFollowing: (FollowUserContent) -> Unit,
) : ListAdapter<FollowUserContent, MyFollowingAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(
        private val binding: ItemFriendManageFollowBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.ivFriendFollow.setOnClickListener {
                onClickFollowing(getItem(absoluteAdapterPosition))
            }
        }

        fun bind(item: FollowUserContent) {
            binding.follower = item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemFriendManageFollowBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<FollowUserContent>() {
            override fun areItemsTheSame(
                oldItem: FollowUserContent,
                newItem: FollowUserContent,
            ): Boolean = oldItem.userId == newItem.userId

            override fun areContentsTheSame(
                oldItem: FollowUserContent,
                newItem: FollowUserContent,
            ): Boolean = oldItem.toString() == newItem.toString()
        }
    }
}
