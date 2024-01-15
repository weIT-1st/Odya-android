package com.weit.presentation.ui.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.weit.domain.model.journal.TravelJournalListInfo
import com.weit.presentation.databinding.ItemPopularSpotSummaryBinding
import com.weit.presentation.model.PopularTravelLog

class PopularSpotAdapter(
    private val navigateTravelLog: (Long) -> Unit,
) : ListAdapter<TravelJournalListInfo, PopularSpotAdapter.PopularSpotViewHolder>(PopularSpotCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularSpotViewHolder {
        return PopularSpotViewHolder(
            ItemPopularSpotSummaryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            ),
        )
    }

    override fun onBindViewHolder(holder: PopularSpotViewHolder, position: Int) {
        val spot = getItem(position)
        holder.bind(spot)
    }

    inner class PopularSpotViewHolder(
        private val binding: ItemPopularSpotSummaryBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        init{
            binding.layoutPopularSpotSummary.setOnClickListener {
                navigateTravelLog(getItem(absoluteAdapterPosition).travelJournalId)
            }
        }

        fun bind(popularTravelLog: TravelJournalListInfo) {
            binding.log = popularTravelLog
        }
    }

    companion object {
        private val PopularSpotCallback: DiffUtil.ItemCallback<TravelJournalListInfo> =
            object : DiffUtil.ItemCallback<TravelJournalListInfo>() {
                override fun areItemsTheSame(
                    oldItem: TravelJournalListInfo,
                    newItem: TravelJournalListInfo,
                ): Boolean {
                    return oldItem.travelJournalId == newItem.travelJournalId
                }

                override fun areContentsTheSame(
                    oldItem: TravelJournalListInfo,
                    newItem: TravelJournalListInfo,
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}
