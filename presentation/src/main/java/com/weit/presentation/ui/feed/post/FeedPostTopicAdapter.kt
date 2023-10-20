package com.weit.presentation.ui.feed.post

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.weit.domain.model.topic.TopicDetail
import com.weit.presentation.databinding.ItemFeedPostTopicBinding

class FeedPostTopicAdapter(
    private var clickTopic : (Long) -> Unit,
) :
    androidx.recyclerview.widget.ListAdapter<TopicDetail, FeedPostTopicAdapter.FeedPostTopicViewHolder>(
        FeedTopicDiffCallback,
    ) {

    class FeedPostTopicViewHolder(
        private val binding: ItemFeedPostTopicBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(topic: TopicDetail) {
            itemView.apply {
                binding.chipTopic.text = topic.topicWord
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedPostTopicViewHolder {
        return FeedPostTopicViewHolder(
            ItemFeedPostTopicBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        )
    }

    override fun onBindViewHolder(holder: FeedPostTopicViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val FeedTopicDiffCallback: DiffUtil.ItemCallback<TopicDetail> =
            object : DiffUtil.ItemCallback<TopicDetail>() {
                override fun areItemsTheSame(oldItem: TopicDetail, newItem: TopicDetail): Boolean {
                    return oldItem.topicId == newItem.topicId
                }

                override fun areContentsTheSame(oldItem: TopicDetail, newItem: TopicDetail): Boolean {
                    return oldItem.topicId == newItem.topicId
                }
            }
    }
}
