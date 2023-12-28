package com.weit.presentation.ui.searchplace.journey

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.weit.domain.model.journal.TravelJournalListInfo
import com.weit.presentation.R
import com.weit.presentation.databinding.ItemMyJounalBinding

class MyJournalAdapter(): ListAdapter<TravelJournalListInfo, MyJournalAdapter.ViewHolder>(diffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemMyJounalBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val myJournalList = getItem(position)
        holder.bind(myJournalList)
    }

    inner class ViewHolder(
        private val binding: ItemMyJounalBinding
    ): RecyclerView.ViewHolder(binding.root){
        fun bind(item: TravelJournalListInfo){
            Glide.with(binding.root.context)
                .load(item.contentImageUrl)
                .into(binding.ivItemMyJourneyThumbnail)

            binding.tvItemMyJournalTitle.text = item.travelJournalTitle
            binding.tvItemMyJournalDate.text = binding.tvItemMyJournalDate.context.getString((R.string.place_journey_date), item.travelStartDate, item.travelStartDate)
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
