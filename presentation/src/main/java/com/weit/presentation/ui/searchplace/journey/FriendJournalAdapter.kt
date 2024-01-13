package com.weit.presentation.ui.searchplace.journey

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.weit.domain.model.journal.TravelJournalListInfo
import com.weit.presentation.R
import com.weit.presentation.databinding.ItemFriendJourneyBinding
import com.weit.presentation.databinding.ItemMyJounalBinding

class FriendJournalAdapter(): ListAdapter<TravelJournalListInfo, FriendJournalAdapter.ViewHolder>(diffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemFriendJourneyBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val myJournalList = getItem(position)
        holder.bind(myJournalList)
    }

    inner class ViewHolder(
        private val binding: ItemFriendJourneyBinding
    ): RecyclerView.ViewHolder(binding.root){
        fun bind(item: TravelJournalListInfo){
            Glide.with(binding.root.context)
                .load(item.contentImageUrl)
                .into(binding.ivItemFriendJourneyThumbnail)

            Glide.with(binding.root.context)
                .load(item.writer.profile)
                .into(binding.ivItemFriendJourneyThumbnail)

            binding.tvItemFiendJourneyTitle.text = item.travelJournalTitle
            binding.tvItemFriendJourneyName.text = item.writer.nickname
            binding.tvItemFriendJourneyDate.text = item.travelStartDate
        }
    }
    companion object{
        private val diffUtil = object : DiffUtil.ItemCallback<TravelJournalListInfo>(){
            override fun areItemsTheSame(
                oldItem: TravelJournalListInfo,
                newItem: TravelJournalListInfo
            ): Boolean =
                oldItem.travelJournalId == newItem.travelJournalId

            override fun areContentsTheSame(
                oldItem: TravelJournalListInfo,
                newItem: TravelJournalListInfo
            ): Boolean =
                oldItem == newItem
        }
    }
}
