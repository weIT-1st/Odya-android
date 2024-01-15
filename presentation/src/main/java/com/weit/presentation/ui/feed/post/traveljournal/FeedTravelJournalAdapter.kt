package com.weit.presentation.ui.feed.post.traveljournal

import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.orhanobut.logger.Logger
import com.weit.presentation.R
import com.weit.presentation.databinding.ItemFeedPostTravelLogBinding
import com.weit.presentation.databinding.ItemSelectPlaceBinding
import com.weit.presentation.model.Visibility
import com.weit.presentation.ui.util.Constants

class FeedTravelJournalAdapter(
    private val action: (FeedTravelJournalAction) -> Unit,
) : ListAdapter<FeedTravelJournalEntity, FeedTravelJournalAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(
        private val binding: ItemFeedPostTravelLogBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.layoutFeedTravelLog.setOnClickListener {
                val item = getItem(absoluteAdapterPosition)
                val action = when (item.travelJournal.visibility) {
                    Visibility.PUBLIC.name -> FeedTravelJournalAction.OnClickPublicJournal(item)
                    else -> FeedTravelJournalAction.OnClickPrivateJournal(item)
                }
                action(action)
            }
        }

        fun bind(item: FeedTravelJournalEntity) {
            binding.log = item
            val foregroundDrawable = if (item.isSelected) {
                ContextCompat.getDrawable(binding.root.context, R.drawable.corners_all_8_stroke2_yellow)
            } else {
                null
            }
            binding.root.foreground = foregroundDrawable
            when (item.travelJournal.visibility) {
                Visibility.PUBLIC.name -> {
                    binding.ivFeedTravelLogLock.visibility = View.GONE
                    binding.ivFeedTravelLog.foreground = null
                }
                else -> {
                    binding.ivFeedTravelLogLock.visibility = View.VISIBLE
                    binding.ivFeedTravelLog.foreground = ColorDrawable(ContextCompat.getColor(binding.root.context, R.color.background_dim))
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemFeedPostTravelLogBinding.inflate(
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
        private val diffUtil = object : DiffUtil.ItemCallback<FeedTravelJournalEntity>() {
            override fun areItemsTheSame(
                oldItem: FeedTravelJournalEntity,
                newItem: FeedTravelJournalEntity,
            ): Boolean = oldItem.travelJournal.travelJournalId == newItem.travelJournal.travelJournalId

            override fun areContentsTheSame(
                oldItem: FeedTravelJournalEntity,
                newItem: FeedTravelJournalEntity,
            ): Boolean = oldItem == newItem
        }
    }
}
