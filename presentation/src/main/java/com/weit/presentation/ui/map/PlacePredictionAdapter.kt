package com.weit.presentation.ui.map

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.weit.domain.model.place.PlacePrediction
import com.weit.presentation.R
import com.weit.presentation.databinding.PlacePredictionItemBinding
import java.util.*


class PlacePredictionAdapter(
    val onPlaceClickListener : (String) -> Unit
) : ListAdapter<PlacePrediction, PlacePredictionAdapter.PlacePredictionViewHolder>(PlaceDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlacePredictionViewHolder {
        return PlacePredictionViewHolder(
            PlacePredictionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: PlacePredictionViewHolder, position: Int) {
        val place = getItem(position)
        holder.bind(place)
        holder.itemView.setOnClickListener {
            onPlaceClickListener(place.placeId)
        }
    }

    class PlacePredictionViewHolder(
        private val binding: PlacePredictionItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(prediction: PlacePrediction) {

            itemView.apply {
                binding.tvTitle.text = prediction.name
                binding.tvAddress.text = prediction.address
            }
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