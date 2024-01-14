package com.weit.presentation.ui.profile.favoriteplace

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.weit.domain.model.place.PlaceDetail
import com.weit.presentation.databinding.ItemProfileFavoritePlaceBinding

class FavoritePlaceAdapter(
    private val selectPlace: (FavoritePlaceEntity) -> Unit,
    ) : ListAdapter<FavoritePlaceEntity, FavoritePlaceAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(
        private val binding: ItemProfileFavoritePlaceBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        init{
            binding.ivBookmarkJournalStar.setOnClickListener {
                selectPlace(getItem(absoluteAdapterPosition))
            }
        }
        fun bind(item: FavoritePlaceEntity) {
            binding.tvItemPlaceTitle.text = item.placeName
            binding.tvItemPlaceAddress.text = item.placeAddress
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemProfileFavoritePlaceBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<FavoritePlaceEntity>() {
            override fun areItemsTheSame(oldItem: FavoritePlaceEntity, newItem: FavoritePlaceEntity): Boolean =
                oldItem.placeId == newItem.placeId

            override fun areContentsTheSame(oldItem: FavoritePlaceEntity, newItem: FavoritePlaceEntity): Boolean =
                oldItem == newItem
        }
    }
}
