package com.weit.presentation.ui.post.travelfriend

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.weit.domain.model.follow.FollowUserContent
import com.weit.presentation.databinding.ItemTravelFriendSearchBinding

class TravelFriendSearchAdapter(
    private val onAddFriend: (FollowUserContent) -> Unit,
) : ListAdapter<FollowUserContent, TravelFriendSearchAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(
        private val binding: ItemTravelFriendSearchBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.btnTravelFriendSearchAdd.setOnClickListener {
                onAddFriend(getItem(absoluteAdapterPosition))
            }
        }

        fun bind(item: FollowUserContent) {
            binding.follower = item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemTravelFriendSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false),
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
