package com.weit.presentation.ui.journal.memory.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.weit.domain.model.journal.TravelJournalListInfo
import com.weit.presentation.R
import com.weit.presentation.databinding.ItemJournalMemoryMyJournalBinding

class MyJournalAdapter(
    private val showDetail: (Long) -> Unit,
    private val updateBookmarkState: (Long) -> Unit
): ListAdapter<TravelJournalListInfo, MyJournalAdapter.ViewHolder>(diffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(ItemJournalMemoryMyJournalBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class ViewHolder(
        private val binding: ItemJournalMemoryMyJournalBinding
    ): RecyclerView.ViewHolder(binding.root){
        init {
            binding.root.setOnClickListener {
                showDetail( getItem(absoluteAdapterPosition).travelJournalId )
            }

            binding.toggleItemJournalMemoryBookmark.setOnClickListener {
                updateBookmarkState( getItem(absoluteAdapterPosition).travelJournalId )
            }
        }
        fun bind(item: TravelJournalListInfo){
            Glide.with(binding.root.context)
                .load(item.contentImageUrl)
                .into(binding.ivItemJournalMemoryMyJournal)

            binding.includeItemJournalMemoryDetail.tvJournalMemoryDetailBoxTitle.text = item.travelJournalTitle
            binding.includeItemJournalMemoryDetail.tvJournalMemoryDetailBoxPlace.text = item.placeDetail.firstOrNull()?.name ?: ""
            binding.includeItemJournalMemoryDetail.tvJournalMemoryDetailBoxDate.text =
                binding.root.context.getString(R.string.place_journey_date, item.travelStartDate, item.travelEndDate)
            binding.toggleItemJournalMemoryBookmark.isChecked = item.isBookmarked
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
