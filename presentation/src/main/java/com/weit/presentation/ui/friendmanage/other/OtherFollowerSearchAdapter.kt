package com.weit.presentation.ui.friendmanage.other

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.weit.domain.model.follow.FollowUserContent
import com.weit.presentation.R
import com.weit.presentation.databinding.ItemFriendManageDeleteBinding
import com.weit.presentation.databinding.ItemFriendManageFollowBinding
import com.weit.presentation.databinding.ItemTravelFriendSearchBinding

class OtherFollowerSearchAdapter(
    private val onClickFollower: (FollowUserContent) -> Unit,
) : ListAdapter<FollowUserContent, OtherFollowerSearchAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(
        private val binding: ItemFriendManageFollowBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.ivFriendFollow.setOnClickListener {
                onClickFollower(getItem(absoluteAdapterPosition))
            }
        }

        fun bind(item: FollowUserContent) {
            binding.follower = item
            val followImage = if(item.isFollowing) R.drawable.bt_following else R.drawable.bt_unfollow_fill
            binding.ivFriendFollow.setImageResource(followImage)
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
