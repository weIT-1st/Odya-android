package com.weit.presentation.ui.feed

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.weit.presentation.R
import com.weit.presentation.databinding.ItemTopicBinding
import com.weit.presentation.model.feed.FeedTopic

class FavoriteTopicAdapter(
    private val selectTopic: (Long,Int) -> Unit,
) :
    ListAdapter<FeedTopic, FavoriteTopicAdapter.FavoriteTopicViewHolder>(
        FavoriteTopicDiffCallback,
    ) {

    inner class FavoriteTopicViewHolder(
        private val binding: ItemTopicBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(topic: FeedTopic) {
            binding.tvTopic.text = topic.topicWord
            binding.tvTopic.setOnClickListener {
                selectTopic(topic.topicId,absoluteAdapterPosition)
            }

            binding.tvTopic.setBackgroundResource(R.drawable.corners_all_20)

            val context = binding.root.context

            val textColor = if (topic.isChecked) {
                ContextCompat.getColor(context, R.color.label_reversed)
            } else {
                ContextCompat.getColor(context, R.color.label_assistive)
            }

            val bgColor = if (topic.isChecked) {
                ContextCompat.getColor(context, R.color.primary)
            } else {
                ContextCompat.getColor(context, R.color.elevation4)
            }

            val gradientDrawable = GradientDrawable()
            gradientDrawable.shape = GradientDrawable.RECTANGLE
            gradientDrawable.cornerRadii = floatArrayOf(CORNER_RADIUS, CORNER_RADIUS, CORNER_RADIUS, CORNER_RADIUS, CORNER_RADIUS, CORNER_RADIUS, CORNER_RADIUS, CORNER_RADIUS)
            gradientDrawable.setColor(bgColor)

            binding.tvTopic.setTextColor(textColor)
            binding.tvTopic.background = gradientDrawable
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
        private val FavoriteTopicDiffCallback: DiffUtil.ItemCallback<FeedTopic> =
            object : DiffUtil.ItemCallback<FeedTopic>() {
                override fun areItemsTheSame(oldItem: FeedTopic, newItem: FeedTopic): Boolean {
                    return oldItem.topicId == newItem.topicId
                }

                override fun areContentsTheSame(
                    oldItem: FeedTopic,
                    newItem: FeedTopic
                ): Boolean {
                    return oldItem == newItem
                }
            }
        const val CORNER_RADIUS = 100f
    }
}
