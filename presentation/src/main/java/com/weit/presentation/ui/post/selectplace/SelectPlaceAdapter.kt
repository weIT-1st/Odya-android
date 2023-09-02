package com.weit.presentation.ui.post.selectplace

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.weit.domain.model.place.PlacePrediction
import com.weit.presentation.databinding.ItemSelectPlaceBinding

class SelectPlaceAdapter(
    private val action: (SelectPlaceAction) -> Unit,
) : ListAdapter<PlacePrediction, SelectPlaceAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(
        private val binding: ItemSelectPlaceBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                action(SelectPlaceAction.OnClickPlace(getItem(absoluteAdapterPosition)))
            }
        }

        fun bind(item: PlacePrediction) {
            binding.tvSelectPlaceName.text = item.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemSelectPlaceBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            ),
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<PlacePrediction>() {
            override fun areItemsTheSame(
                oldItem: PlacePrediction,
                newItem: PlacePrediction,
            ): Boolean = oldItem.placeId == newItem.placeId

            override fun areContentsTheSame(
                oldItem: PlacePrediction,
                newItem: PlacePrediction,
            ): Boolean = oldItem == newItem
        }
    }
}
