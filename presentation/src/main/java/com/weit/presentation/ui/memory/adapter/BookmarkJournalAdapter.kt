package com.weit.presentation.ui.memory.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.weit.domain.model.bookmark.JournalBookMarkInfo
import com.weit.presentation.databinding.ItemJournalMemoryBookmarkJournalBinding

class BookmarkJournalAdapter(
    private val showDetail: (Long) -> Unit
): ListAdapter<JournalBookMarkInfo, BookmarkJournalAdapter.ViewHolder>(diffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(ItemJournalMemoryBookmarkJournalBinding.inflate(LayoutInflater.from(parent.context), parent, false))


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class ViewHolder(
        private val binding: ItemJournalMemoryBookmarkJournalBinding
    ): RecyclerView.ViewHolder(binding.root){
        init {
            binding.root.setOnClickListener {
                showDetail( getItem(absoluteAdapterPosition).travelJournalId )
            }

            binding.toggleItemJournalMemoryBookmark.setOnClickListener {
                // todo bookmark
            }
        }

        fun bind(item: JournalBookMarkInfo){
            Glide.with(binding.root.context)
                .load(item.travelJournalMainImageUrl)
                .into(binding.ivItemJournalMemoryBookmark)

            Glide.with(binding.root.context)
                .load(item.writer.profile)
                .into(binding.includeItemJournalMemoryDetail.ivJournalMemoryDetailProfile)

            binding.includeItemJournalMemoryDetail.tvJournalMemoryDetailBoxTitle.text = item.title
            binding.includeItemJournalMemoryDetail.tvJournalMemoryDetailBoxDate.text = item.travelStartDate
        }
    }

    companion object{
        private val diffUtil = object : DiffUtil.ItemCallback<JournalBookMarkInfo>() {
            override fun areItemsTheSame(
                oldItem: JournalBookMarkInfo,
                newItem: JournalBookMarkInfo
            ): Boolean =
                oldItem.travelJournalId == newItem.travelJournalId

            override fun areContentsTheSame(
                oldItem: JournalBookMarkInfo,
                newItem: JournalBookMarkInfo
            ): Boolean =
                oldItem == newItem

        }
    }
}
