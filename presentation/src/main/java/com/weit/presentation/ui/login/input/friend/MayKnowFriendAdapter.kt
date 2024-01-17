package com.weit.presentation.ui.login.input.friend

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.weit.domain.model.follow.FollowUserContent
import com.weit.presentation.databinding.ItemMayknowFriendSummaryBinding

class MayKnowFriendAdapter(
    private val createFollow : (Long) -> Unit
) : ListAdapter<FollowUserContent, MayKnowFriendAdapter.ViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemMayknowFriendSummaryBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class ViewHolder(
        private val binding: ItemMayknowFriendSummaryBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item : FollowUserContent) {

            Glide.with(binding.root.context)
                .load(item.profile.url)
                .into(binding.ivMayknowFriendSummaryProfile)

            binding.tvMayknowFriendSummaryNickname.text = item.nickname
            binding.btnMayknowFriendSummaryFollow.setOnClickListener {
                createFollow(item.userId)
            }
        }
    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<FollowUserContent>() {
            override fun areItemsTheSame(
                oldItem: FollowUserContent,
                newItem: FollowUserContent
            ): Boolean =
                oldItem.userId == newItem.userId

            override fun areContentsTheSame(
                oldItem: FollowUserContent,
                newItem: FollowUserContent
            ): Boolean =
                oldItem == newItem
        }
    }
}
