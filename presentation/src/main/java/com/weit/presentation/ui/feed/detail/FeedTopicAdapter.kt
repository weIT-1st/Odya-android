package com.weit.presentation.ui.feed.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.weit.domain.model.topic.TopicDetail
import com.weit.presentation.R
import com.weit.presentation.databinding.ItemFeedDetailTopicBinding
import com.weit.presentation.model.TopicDTO

class FeedTopicAdapter() :
    androidx.recyclerview.widget.ListAdapter<TopicDTO, FeedTopicAdapter.FeedTopicViewHolder>(
        TopicDiffCallback,
    ) {

    class FeedTopicViewHolder(
        private val binding: ItemFeedDetailTopicBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(topic: TopicDetail) {
            binding.tvTopic.text =
                binding.root.context.getString(R.string.feed_detail_topic, topic.topicWord)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedTopicViewHolder {
        return FeedTopicViewHolder(
            ItemFeedDetailTopicBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        )
    }

    override fun onBindViewHolder(holder: FeedTopicViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val TopicDiffCallback: DiffUtil.ItemCallback<TopicDTO> =
            object : DiffUtil.ItemCallback<TopicDTO>() {
                override fun areItemsTheSame(oldItem: TopicDTO, newItem: TopicDTO): Boolean {
                    return oldItem.topicId == newItem.topicId
                }

                override fun areContentsTheSame(
                    oldItem: TopicDTO,
                    newItem: TopicDTO,
                ): Boolean {
                    return oldItem.topicId == newItem.topicId
                }
            }
    }
}
