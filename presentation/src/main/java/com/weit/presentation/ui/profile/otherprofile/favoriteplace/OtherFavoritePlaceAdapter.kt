package com.weit.presentation.ui.profile.otherprofile.favoriteplace

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.weit.presentation.R
import com.weit.presentation.databinding.ItemProfileFavoritePlaceBinding

class OtherFavoritePlaceAdapter(
    private val selectPlace: (OtherFavoritePlaceEntity) -> Unit,
    ) : ListAdapter<OtherFavoritePlaceEntity, OtherFavoritePlaceAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(
        private val binding: ItemProfileFavoritePlaceBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        init{
            binding.ivBookmarkJournalStar.setOnClickListener {
                selectPlace(getItem(absoluteAdapterPosition))
            }
        }
        fun bind(item: OtherFavoritePlaceEntity) {
            val starImage = if(item.isFavoritePlace) R.drawable.ic_bookmark_fill else R.drawable.ic_bookmark
            binding.ivBookmarkJournalStar.setImageResource(starImage)
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
        private val diffUtil = object : DiffUtil.ItemCallback<OtherFavoritePlaceEntity>() {
            override fun areItemsTheSame(oldItem: OtherFavoritePlaceEntity, newItem: OtherFavoritePlaceEntity): Boolean =
                oldItem.placeId == newItem.placeId

            override fun areContentsTheSame(oldItem: OtherFavoritePlaceEntity, newItem: OtherFavoritePlaceEntity): Boolean =
                oldItem == newItem
        }
    }
}
