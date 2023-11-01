package com.weit.presentation.ui.feed.post

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.weit.domain.model.topic.TopicDetail
import com.weit.presentation.R
import com.weit.presentation.databinding.ItemDailyTravelLogPictureBinding
import com.weit.presentation.databinding.ItemFeedDetailTopicBinding
import com.weit.presentation.databinding.ItemFeedPostImageBinding
import com.weit.presentation.model.TopicDTO
import com.weit.presentation.ui.post.travellog.DailyTravelLogPictureAdapter

class FeedImageAdapter(
) : ListAdapter<String, FeedImageAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(
        private val binding: ItemFeedPostImageBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: String) {
            Glide.with(binding.root)
                .load(item)
                .into(binding.ivFeedPost)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemFeedPostImageBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean =
                oldItem == newItem
        }
    }
}
