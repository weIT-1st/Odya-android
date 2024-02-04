package com.weit.presentation.ui.profile.reptraveljournal

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.weit.domain.model.journal.TravelJournalCompanionsInfo
import com.weit.presentation.R
import com.weit.presentation.databinding.ItemTogetherFriendBinding

class TogetherFriendAdapter(
    private val selectFriend: (TravelJournalCompanionsInfo) -> Unit,
): ListAdapter<TravelJournalCompanionsInfo, TogetherFriendAdapter.ViewHolder>(
    diffUtil
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
            = ViewHolder(ItemTogetherFriendBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class ViewHolder(
        private val binding: ItemTogetherFriendBinding
    ): RecyclerView.ViewHolder(binding.root){
        init{
            binding.ivFriendFollow.setOnClickListener {
                selectFriend(getItem(absoluteAdapterPosition))
            }
        }
        fun bind(item: TravelJournalCompanionsInfo) {
            binding.tvFriendName.text = item.nickname
            Glide.with(binding.root.context)
                .load(item.profileUrl)
                .placeholder(R.color.system_inactive)
                .into(binding.ivFriendProfile)
            val starImage = if(item.isFollowing) R.drawable.bt_following else R.drawable.bt_follow
            binding.ivFriendFollow.setImageResource(starImage)
        }
    }
    companion object{
        private val diffUtil = object : DiffUtil.ItemCallback<TravelJournalCompanionsInfo>() {
            override fun areItemsTheSame(
                oldItem: TravelJournalCompanionsInfo,
                newItem: TravelJournalCompanionsInfo
            ): Boolean =
                oldItem.userId == newItem.userId

            override fun areContentsTheSame(
                oldItem: TravelJournalCompanionsInfo,
                newItem: TravelJournalCompanionsInfo
            ): Boolean =
                oldItem == newItem
        }
    }

}
