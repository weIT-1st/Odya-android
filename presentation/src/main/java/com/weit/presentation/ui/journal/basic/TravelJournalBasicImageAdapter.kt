package com.weit.presentation.ui.journal.basic

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.weit.domain.model.journal.TravelJournalContentsImagesInfo
import com.weit.presentation.databinding.ItemJournalDetailBasicImageBinding

class TravelJournalBasicImageAdapter: ListAdapter<TravelJournalContentsImagesInfo, TravelJournalBasicImageAdapter.ViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(ItemJournalDetailBasicImageBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    class ViewHolder(
        private val binding: ItemJournalDetailBasicImageBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: TravelJournalContentsImagesInfo) {
            Glide.with(binding.root.context)
                .load(item.contentImageUrl)
                .into(binding.ivItemJournalDetailBasicImage)
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
