package com.weit.presentation.ui.memory.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.weit.domain.model.journal.TravelJournalListInfo
import com.weit.presentation.R
import com.weit.presentation.databinding.ItemJournalMemoryMyJournalBinding

class MyJournalAdapter: ListAdapter<TravelJournalListInfo, MyJournalAdapter.ViewHolder>(diffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(ItemJournalMemoryMyJournalBinding.inflate(LayoutInflater.from(parent.context), parent, false))


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class ViewHolder(
        private val binding: ItemJournalMemoryMyJournalBinding
    ): RecyclerView.ViewHolder(binding.root){
        fun bind(item: TravelJournalListInfo){
            Glide.with(binding.root.context)
                .load(item.contentImageUrl)
                .into(binding.ivItemJournalMemoryMyJournal)

            binding.includeItemJournalMemoryDetail.tvJournalMemoryDetailBoxTitle.text = item.travelJournalTitle
            binding.includeItemJournalMemoryDetail.tvJournalMemoryDetailBoxDate.text =
                binding.root.context.getString(R.string.place_journey_date, item.travelStartDate, item.travelEndDate)
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
