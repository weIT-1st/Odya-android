package com.weit.presentation.ui.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.weit.presentation.databinding.ItemPopularSpotSummaryBinding
import com.weit.presentation.model.PopularTravelLog

class PopularSpotAdapter(
    private val goToTravelLog: (Long) -> Unit,
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
        holder.spot.setOnClickListener {
            goToTravelLog(spot.travelLogId)
        }
    }

    inner class PopularSpotViewHolder(
        private val binding: ItemPopularSpotSummaryBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        val spot = binding.layoutPopularSpotSummary
        fun bind(popularTravelLog: PopularTravelLog) {
//             Glide.with(binding.root)
//                .load(popularTravelLog.userProfile)
//                .into(binding.ivPopularSpotSummaryProfile)
            binding.tvPopularSpotSummaryTitle.text = popularTravelLog.travelLogTitle
            binding.tvPopularSpotSummaryNickname.text = popularTravelLog.userNickname
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
