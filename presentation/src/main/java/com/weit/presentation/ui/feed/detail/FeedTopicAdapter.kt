package com.weit.presentation.ui.feed.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.weit.domain.model.topic.TopicDetail
import com.weit.presentation.R
import com.weit.presentation.databinding.ItemFeedDetailTopicBinding

class FeedTopicAdapter() :
    androidx.recyclerview.widget.ListAdapter<TopicDetail, FeedTopicAdapter.FeedTopicViewHolder>(
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
        private val TopicDiffCallback: DiffUtil.ItemCallback<TopicDetail> =
            object : DiffUtil.ItemCallback<TopicDetail>() {
                override fun areItemsTheSame(oldItem: TopicDetail, newItem: TopicDetail): Boolean {
                    return oldItem.topicId == newItem.topicId
                }

                override fun areContentsTheSame(
                    oldItem: TopicDetail,
                    newItem: TopicDetail,
                ): Boolean {
                    return oldItem.topicId == newItem.topicId
                }
            }
    }
}
