package com.weit.presentation.ui.post.selectplace

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.weit.presentation.databinding.ItemSelectPlaceBinding

class SelectPlaceAdapter(
    private val action: (SelectPlaceAction) -> Unit,
) : ListAdapter<SelectPlaceEntity, SelectPlaceAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(
        private val binding: ItemSelectPlaceBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                action(SelectPlaceAction.OnClickPlace(getItem(absoluteAdapterPosition).place))
            }
        }

        fun bind(item: SelectPlaceEntity) {
            binding.tvSelectPlaceName.text = item.place.name
            binding.tvSelectPlaceAddress.text = item.place.address
            binding.ivSelectPlaceChecked.isVisible = item.isSelected
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
        private val diffUtil = object : DiffUtil.ItemCallback<SelectPlaceEntity>() {
            override fun areItemsTheSame(
                oldItem: SelectPlaceEntity,
                newItem: SelectPlaceEntity,
            ): Boolean = oldItem.place.placeId == newItem.place.placeId

            override fun areContentsTheSame(
                oldItem: SelectPlaceEntity,
                newItem: SelectPlaceEntity,
            ): Boolean = oldItem == newItem
        }
    }
}
