package com.weit.presentation.ui.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.weit.presentation.databinding.ItemPopularSpotSummaryBinding
import com.weit.presentation.model.PopularTravelLog

class PopularSpotAdapter(
    private val navigateTravelLog: (Long) -> Unit,
) : ListAdapter<PopularTravelLog, PopularSpotAdapter.PopularSpotViewHolder>(PopularSpotCallback) {

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

        fun bind(popularTravelLog: PopularTravelLog) {
            binding.log = popularTravelLog
            binding.layoutPopularSpotSummary.setOnClickListener {
                navigateTravelLog(popularTravelLog.travelLogId)
            }
        }
    }

    companion object {
        private val PopularSpotCallback: DiffUtil.ItemCallback<PopularTravelLog> =
            object : DiffUtil.ItemCallback<PopularTravelLog>() {
                override fun areItemsTheSame(
                    oldItem: PopularTravelLog,
                    newItem: PopularTravelLog,
                ): Boolean {
                    return oldItem.travelLogId == newItem.travelLogId
                }

                override fun areContentsTheSame(
                    oldItem: PopularTravelLog,
                    newItem: PopularTravelLog,
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}
