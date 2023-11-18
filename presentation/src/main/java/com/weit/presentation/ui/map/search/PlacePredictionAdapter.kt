package com.weit.presentation.ui.map.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.weit.domain.model.place.PlacePrediction
import com.weit.presentation.databinding.ItemPlaceAutoCompleteBinding

class PlacePredictionAdapter(
    val onPlaceClickListener: (String, String) -> Unit,
) : ListAdapter<PlacePrediction, PlacePredictionAdapter.ViewHolder>(PlaceDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemPlaceAutoCompleteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val place = getItem(position)
        holder.bind(place)
        holder.itemView.setOnClickListener {
            onPlaceClickListener(place.placeId, place.name)
        }
    }

    class ViewHolder(
        private val binding: ItemPlaceAutoCompleteBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(prediction: PlacePrediction) {
            binding.tvItemPlaceTitle.text = prediction.name
            binding.tvItemPlaceAddress.text = prediction.address
        }
    }
    companion object {
        private val PlaceDiffCallback: DiffUtil.ItemCallback<PlacePrediction> =
            object : DiffUtil.ItemCallback<PlacePrediction>() {
                override fun areItemsTheSame(oldItem: PlacePrediction, newItem: PlacePrediction): Boolean {
                    return oldItem.placeId == newItem.placeId
                }

                override fun areContentsTheSame(oldItem: PlacePrediction, newItem: PlacePrediction): Boolean {
                    return oldItem == newItem
                }
            }
    }
}
