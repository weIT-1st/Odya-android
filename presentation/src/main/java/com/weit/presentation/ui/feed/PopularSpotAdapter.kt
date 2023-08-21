package com.weit.presentation.ui.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.weit.presentation.model.PopularSpot
import com.weit.presentation.databinding.ItemPopularSpotSummaryBinding

class PopularSpotAdapter(
) : ListAdapter<PopularSpot, PopularSpotAdapter.PopularSpotViewHolder>(PopularSpotCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularSpotViewHolder {
        return PopularSpotViewHolder(
            ItemPopularSpotSummaryBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        )
    }

    override fun onBindViewHolder(holder: PopularSpotViewHolder, position: Int) {
        val spot = getItem(position)
        holder.bind(spot)
    }

    class PopularSpotViewHolder(
        private val binding: ItemPopularSpotSummaryBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(popularSpot: PopularSpot) {
            binding.tvPopularSpotSummaryTitle.text = popularSpot.title
            binding.tvPopularSpotSummaryNickname.text = popularSpot.nickname

        }
    }
    companion object {
        private val PopularSpotCallback: DiffUtil.ItemCallback<PopularSpot> =
            object : DiffUtil.ItemCallback<PopularSpot>() {
                override fun areItemsTheSame(oldItem: PopularSpot, newItem: PopularSpot): Boolean {
                    return oldItem.title == newItem.title
                }

                override fun areContentsTheSame(oldItem: PopularSpot, newItem: PopularSpot): Boolean {
                    return oldItem == newItem
                }
            }
    }
}
