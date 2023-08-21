package com.weit.presentation.ui.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.weit.domain.model.topic.TopicDetail
import com.weit.presentation.databinding.ItemTopicBinding

class FavoriteTopicAdapter() :
    androidx.recyclerview.widget.ListAdapter<TopicDetail, FavoriteTopicAdapter.FavoriteTopicViewHolder>(
        FavoriteTopicDiffCallback
    ) {

    class FavoriteTopicViewHolder(
        private val binding: ItemTopicBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(topic: TopicDetail) {
            itemView.apply {
                binding.tvTopic.text = topic.topicWord
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteTopicViewHolder {
        return FavoriteTopicViewHolder(
            ItemTopicBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        )
    }

    override fun onBindViewHolder(holder: FavoriteTopicViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val FavoriteTopicDiffCallback: DiffUtil.ItemCallback<TopicDetail> =
            object : DiffUtil.ItemCallback<TopicDetail>() {
                override fun areItemsTheSame(oldItem: TopicDetail, newItem: TopicDetail): Boolean {
                    return oldItem.topicWord== newItem.topicWord
                }

                override fun areContentsTheSame(oldItem: TopicDetail, newItem: TopicDetail): Boolean {
                    return oldItem == newItem
                }
            }
    }


}