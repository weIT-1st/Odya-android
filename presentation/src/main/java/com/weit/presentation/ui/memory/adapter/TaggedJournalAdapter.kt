package com.weit.presentation.ui.memory.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.weit.domain.model.journal.TravelJournalListInfo
import com.weit.presentation.databinding.ItemJournalMemoryTagJournalBinding

class TaggedJournalAdapter: ListAdapter<TravelJournalListInfo, TaggedJournalAdapter.ViewHolder>(diffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(ItemJournalMemoryTagJournalBinding.inflate(LayoutInflater.from(parent.context), parent, false))


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class ViewHolder(
        private val binding: ItemJournalMemoryTagJournalBinding
    ): RecyclerView.ViewHolder(binding.root){
        fun bind(item: TravelJournalListInfo){
            Glide.with(binding.root.context)
                .load(item.contentImageUrl)
                .into(binding.ivItemJournalMemoryLastJournal)

            Glide.with(binding.root.context)
                .load(item.writer.profile)
                .into(binding.includeItemJournalMemoryDetail.ivJournalMemoryDetailProfile)

            binding.includeItemJournalMemoryDetail.tvJournalMemoryDetailBoxTitle.text = item.travelJournalTitle
            binding.includeItemJournalMemoryDetail.tvJournalMemoryDetailBoxDate.text = item.travelStartDate
        }
    }

    companion object{
        private val diffUtil = object : DiffUtil.ItemCallback<TravelJournalListInfo>() {
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
