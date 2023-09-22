package com.weit.presentation.ui.post.selectplace

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.weit.presentation.R
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
            val textColor = getTextColor(item.isSelected)
            binding.tvSelectPlaceName.run {
                setTextColor(textColor)
                text = item.place.name
            }
            binding.tvSelectPlaceAddress.run {
                setTextColor(textColor)
                text = item.place.address
            }
            binding.root.backgroundTintList = getBackgroundTint(item.isSelected)
            binding.ivSelectPlaceChecked.setImageResource(getCheckIcon(item.isSelected))
        }

        private fun getTextColor(isSelected: Boolean): Int {
            val color = if (isSelected) {
                R.color.background_normal
            } else {
                R.color.label_normal
            }
            return binding.root.context.resources.getColor(color, null)
        }

        private fun getBackgroundTint(isSelected: Boolean): ColorStateList? {
            val color = if (isSelected) {
                R.color.primary
            } else {
                R.color.background_normal
            }
            return ContextCompat.getColorStateList(binding.root.context, color)
        }

        private fun getCheckIcon(isSelected: Boolean): Int {
            return if (isSelected) {
                R.drawable.ic_select
            } else {
                R.drawable.ic_unselect
            }
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
