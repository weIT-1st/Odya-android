package com.weit.presentation.ui.searchplace.journey

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.weit.domain.model.journal.TravelJournalInfo
import com.weit.domain.model.journal.TravelJournalListInfo
import com.weit.presentation.R
import com.weit.presentation.databinding.ItemMyJounalBinding

class MyJournalAdapter(
    private val placeId: String
): ListAdapter<TravelJournalInfo, MyJournalAdapter.ViewHolder>(diffUtil) {
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
        fun bind(item: TravelJournalInfo){
            binding.tvItemMyJournalTitle.text = item.travelJournalTitle
            binding.tvItemMyJournalDate.text = binding.root.context.getString(R.string.place_journey_date, item.travelStartDate, item.travelEndDate)
            binding.tvTabPlaceMyJourneyContent.text = item.travelJournalContents.find { it.placeId == placeId }?.content

            // todo 친구 더보기 -> 여행일지 조회 pr에 있습니다 머지되면 추가하겠습니다.
        }
    }
    companion object{
        private val diffUtil = object : DiffUtil.ItemCallback<TravelJournalInfo>(){
            override fun areItemsTheSame(
                oldItem: TravelJournalInfo,
                newItem: TravelJournalInfo
            ): Boolean =
                oldItem.travelJournalId == newItem.travelJournalId

            override fun areContentsTheSame(
                oldItem: TravelJournalInfo,
                newItem: TravelJournalInfo
            ): Boolean =
                oldItem == newItem
        }
    }
}
