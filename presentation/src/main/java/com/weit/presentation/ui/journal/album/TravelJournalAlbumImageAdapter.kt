package com.weit.presentation.ui.journal.album

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.weit.domain.model.journal.TravelJournalContentsImagesInfo
import com.weit.presentation.databinding.ItemJournalDetailAlbumImageBinding

class TravelJournalAlbumImageAdapter: ListAdapter<TravelJournalContentsImagesInfo, TravelJournalAlbumImageAdapter.ViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(ItemJournalDetailAlbumImageBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    class ViewHolder(
        private val binding: ItemJournalDetailAlbumImageBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: TravelJournalContentsImagesInfo) {
            Glide.with(binding.root.context)
                .load(item.contentImageUrl)
                .into(binding.ivItemJournalDetailAlbumImage)
        }
    }

    companion object{
        private val diffUtil = object : DiffUtil.ItemCallback<TravelJournalContentsImagesInfo>() {
            override fun areItemsTheSame(
                oldItem: TravelJournalContentsImagesInfo,
                newItem: TravelJournalContentsImagesInfo
            ): Boolean =
                oldItem.travelJournalContentImageId == newItem.travelJournalContentImageId

            override fun areContentsTheSame(
                oldItem: TravelJournalContentsImagesInfo,
                newItem: TravelJournalContentsImagesInfo
            ): Boolean =
                oldItem == newItem
        }
    }
}
