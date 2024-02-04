package com.weit.presentation.ui.profile.bookmarkjournal

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.weit.domain.model.bookmark.JournalBookMarkInfo
import com.weit.presentation.R
import com.weit.presentation.databinding.ItemBookmarkTravelJournalBinding
import com.weit.presentation.databinding.ItemJournalMemoryBookmarkJournalBinding

class ProfileBookmarkJournalAdapter(
    private val showDetail: (Long) -> Unit,
    private val updateBookmarkState: (JournalBookMarkInfo) -> Unit
): ListAdapter<JournalBookMarkInfo, ProfileBookmarkJournalAdapter.ViewHolder>(diffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(ItemBookmarkTravelJournalBinding.inflate(LayoutInflater.from(parent.context), parent, false))


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class ViewHolder(
        private val binding: ItemBookmarkTravelJournalBinding
    ): RecyclerView.ViewHolder(binding.root){
        init {
            binding.root.setOnClickListener {
                showDetail( getItem(absoluteAdapterPosition).travelJournalId )
            }

            binding.ivBookmarkJournalStar.setOnClickListener {
               updateBookmarkState( getItem(absoluteAdapterPosition) )
            }
        }

        fun bind(item: JournalBookMarkInfo){
            val starImage = if(item.isBookmarked) R.drawable.ic_star_yellow_fill else R.drawable.ic_star_white_empty
            binding.ivBookmarkJournalStar.setImageResource(starImage)
            Glide.with(binding.root.context)
                .load(item.travelJournalMainImageUrl)
                .into(binding.ivItemJourneyThumbnail)
            binding.journal = item
            binding.tvItemFiendJourneyTitle.text = item.title
            binding.tvItemFriendJourneyName.text = item.writer.nickname
            binding.tvItemFriendJourneyDate.text = item.travelStartDate
        }
    }

    companion object{
        private val diffUtil = object : DiffUtil.ItemCallback<JournalBookMarkInfo>() {
            override fun areItemsTheSame(
                oldItem: JournalBookMarkInfo,
                newItem: JournalBookMarkInfo
            ): Boolean =
                oldItem.travelJournalBookMarkId == newItem.travelJournalBookMarkId

            override fun areContentsTheSame(
                oldItem: JournalBookMarkInfo,
                newItem: JournalBookMarkInfo
            ): Boolean =
                oldItem == newItem

        }
    }
}
