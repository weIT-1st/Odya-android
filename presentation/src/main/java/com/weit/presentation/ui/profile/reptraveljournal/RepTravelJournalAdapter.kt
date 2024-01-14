package com.weit.presentation.ui.profile.reptraveljournal

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.weit.domain.model.reptraveljournal.RepTravelJournalListInfo
import com.weit.presentation.R
import com.weit.presentation.databinding.ItemProfileRepTravelJournalBinding

class RepTravelJournalAdapter(
    private val selectTravelJournal : (Long) -> Unit
): ListAdapter<RepTravelJournalListInfo, RepTravelJournalAdapter.ViewHolder>(diffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemProfileRepTravelJournalBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val myJournalList = getItem(position)
        holder.bind(myJournalList)
    }

    inner class ViewHolder(
        private val binding: ItemProfileRepTravelJournalBinding
    ): RecyclerView.ViewHolder(binding.root){

        init{
            binding.root.setOnClickListener {
                selectTravelJournal(getItem(absoluteAdapterPosition).travelJournalId)
            }
        }
        fun bind(item: RepTravelJournalListInfo){
            binding.tvItemMyJournalTitle.text = item.title
            binding.tvItemMyJournalDate.text = binding.root.context.getString(R.string.place_journey_date, item.travelStartDate, item.travelEndDate)
//            binding.tvTabPlaceMyJourneyContent.text = item.travelJournalContents.find { it.placeId == placeId }?.content
             //TODO 지도,친구
        }
    }
    companion object{
        private val diffUtil = object : DiffUtil.ItemCallback<RepTravelJournalListInfo>(){
            override fun areItemsTheSame(
                oldItem: RepTravelJournalListInfo,
                newItem: RepTravelJournalListInfo
            ): Boolean =
                oldItem.repTravelJournalId == newItem.repTravelJournalId

            override fun areContentsTheSame(
                oldItem: RepTravelJournalListInfo,
                newItem: RepTravelJournalListInfo
            ): Boolean =
                oldItem == newItem
        }
    }
}
